package com.young.microservices.mlagenteval.common.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.idrsolutions.image.png.PngCompressor;
import com.young.microservices.mlagenteval.common.CommonResponse;
import com.young.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.young.microservices.mlagenteval.common.event.JobExecEvent;
import com.young.microservices.mlagenteval.common.event.JobStatusEvent;
import com.young.microservices.mlagenteval.common.event.JobTrackDetailEvent;
import com.young.microservices.mlagenteval.common.websocket.MessageSendUtils;
import com.young.microservices.mlagenteval.common.websocket.WebSocketSessionManager;
import com.young.microservices.mlagenteval.constant.Constants;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.young.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import com.young.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceTrackMapper;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.enums.*;
import com.young.microservices.mlagenteval.exception.BusinessException;
import com.young.microservices.mlagenteval.service.AgentEvalOperateService;
import com.young.microservices.mlagenteval.service.NfsService;
import com.in28minutes.microservices.mlagenteval.utils.*;
import com.young.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import com.young.microservices.mlagenteval.dto.*;
import com.young.microservices.mlagenteval.enums.*;
import com.young.microservices.mlagenteval.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
public class JobExecEventHandler {
    AgentEvalJobInstanceTrackMapper agentEvalJobInstanceTrackMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceTrackMapper.class);
    AgentEvalJobInstanceMapper instanceMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceMapper.class);
    AgentEvalOperateService agentEvalOperateService = SpringBeanUtils.getBean(AgentEvalOperateService.class);
    WebSocketSessionManager webSocketSessionManager = SpringBeanUtils.getBean(WebSocketSessionManager.class);

    @Subscribe
    public void handle(JobExecEvent jobExecEvent) {
        List<InstanceTaskInfo> instanceTaskInfos = jobExecEvent.getJobDetail().getInstanceTaskInfos();
        if (CollectionUtils.isEmpty(instanceTaskInfos)) {
            log.info("no job dataset executable....");
            return;
        }
        TenantNasInfo tenantNasInfo = jobExecEvent.getJobDetail().getTenantNasInfo();
        NfsService nfsService = new NfsService(tenantNasInfo.getNasAddress(), tenantNasInfo.getNasDir());
        AgentEvalJobDetail jobDetail = jobExecEvent.getJobDetail();
        DeviceInfo device = jobDetail.getDeviceInfoList().get(0);

        // execute turn, every turn will generate an instance
        Integer executeTurn = jobDetail.getExecuteTurn();
        IntStream.rangeClosed(1, executeTurn).forEach(curr -> {
            log.info("current execute turn: [{}]", curr);
            AgentEvalJobInstanceDo jobInstanceDo = getAgentEvalJobInstanceDo(jobDetail, device, curr);

            String instanceId = jobInstanceDo.getId();
            String jobId = jobInstanceDo.getJobId();
            log.info("jobStatusEvent: instanceId[{}]", instanceId);
            JobEventRegisterCenter.post(new JobStatusEvent(instanceId, "RUNNING", curr, null, ""));

            // request to get screen size.
            ADBCMDReq adbcmdReq = new ADBCMDReq();
            adbcmdReq.setUdId(device.getDeviceUdid());
            adbcmdReq.setCmd(ADBCommand.WM_SIZE.getValue());
            CommonResponse resp = agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            String screenSize = String.valueOf(resp.getData());
            if (StringUtils.isEmpty(screenSize)) {
                throw new BusinessException(BizErrorCode.SERVER_ERROR, "get screen size failed, pls check device.");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
            }

            int taskCounter = 0;
            Iterator<InstanceTaskInfo> iterator = instanceTaskInfos.iterator();
            while (iterator.hasNext()) {
                InstanceTaskInfo taskInfo = iterator.next();
                taskCounter++;

                AgentEvalJobInstanceTrackDo jobInstanceTrackDo = buildAgentEvalJobInstanceTrackDo(taskInfo, jobInstanceDo, adbcmdReq);
                int stepCounter = 0;
                List<String> inferList = new ArrayList<>();
                while (true) {
                    String resultPrompt = checkPrompt(jobDetail, taskInfo, stepCounter);
                    stepCounter++;
                    AgentEvalJobInstanceDo currInstanceDo = instanceMapper.selectById(instanceId);
                    if (currInstanceDo.getJobStatus().equals(JobStatusEnum.SHUTDOWN.getValue())) {
                        return;
                    }
                    // obtain current screenshot, and upload the img to the nas, replace the last screenshot
                    // then upload the nas dir:
                    adbcmdReq.setCmd(ADBCommand.SCREENSHOT.getValue());
                    agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
                    }

                    adbcmdReq.setCmd(ADBCommand.PULL_SCREENSHOT.getValue());
                    adbcmdReq.setScreenshotPath(FilePathUtils.buildFilePath(jobDetail.getTenantCode(), jobId, instanceId, jobInstanceTrackDo.getId(), Constants.CACHE));
                    agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                    log.info("current step is: [{}]", stepCounter);
                    // start model inference by calling model
                    String cachePath = FilePathUtils.buildFilePath(tenantNasInfo.getNasDir(), jobDetail.getTenantCode(), jobId, instanceId,
                            jobInstanceTrackDo.getId());
                    String filePath = FilePathUtils.buildFilePath(cachePath, Constants.CACHE, Constants.SCREENSHOT_PNG);
                    compressImg(filePath);
                    String nasDir = tenantNasInfo.getNasDir();
                    String tenantCode = jobDetail.getTenantCode();

                    String lastDestDir = FilePathUtils.buildFilePath(nasDir, tenantCode, jobId, instanceId,
                            jobInstanceTrackDo.getId(), "step-".concat(String.valueOf(stepCounter - 1)));
                    String currDestDir = FilePathUtils.buildFilePath(nasDir, tenantCode, jobId, instanceId,
                            jobInstanceTrackDo.getId(), "step-".concat(String.valueOf(stepCounter)));

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
                    }

                    copyNfsFile(filePath, nfsService, currDestDir, lastDestDir, stepCounter);

                    InputStream inputStream = nfsService.downloadNfsFile(filePath);

                    String inferResp = agentEvalOperateService.execInference(inputStream, resultPrompt, jobInstanceDo.getId());
                    log.info("inferResp:[{}]", inferResp);
                    if (Objects.isNull(inferResp)) {
                        JobEventRegisterCenter.post(new JobStatusEvent(instanceId, JobStatusEnum.FAILED.getValue(), curr, LocalDateTime.now(), "model inference failed."));
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, "model inference failed.");
                    }

                    String specAction = agentEvalOperateService.obtainSpecAction(inferResp, instanceId);
                    // Execute parsed actions; int[] screenSizeArr = {1080, 2340}; // 示例屏幕尺寸
                    int[] screenSizeArr = agentEvalOperateService.parseScreenSize(screenSize);
                    log.info("specAction:[{}]", specAction);
                    drawOnImageByAction(FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, filePath), currDestDir, specAction, screenSizeArr);

                    String imgPathBefore = buildImgPath(tenantNasInfo, jobDetail.getTenantCode(), jobId, instanceId, jobInstanceTrackDo.getId(), stepCounter, Constants.SCREENSHOT_PNG);
                    String imgPathAfter = buildImgPath(tenantNasInfo, jobDetail.getTenantCode(), jobId, instanceId, jobInstanceTrackDo.getId(), stepCounter, Constants.LAST_SCREENSHOT_PNG);
                    String inferResponse = buildInferResponse(inferResp, stepCounter, imgPathBefore, imgPathAfter);
                    inferList.add(inferResponse);

                    JobEventRegisterCenter.post(new JobTrackDetailEvent(jobInstanceTrackDo.getId(), inferList, String.valueOf(stepCounter)));

                    boolean execRes = executeAction(specAction, screenSizeArr, jobInstanceDo, adbcmdReq);
                    log.info("=========================== step: " + stepCounter + " exec finished =======================");
                    log.info("Action execRes:[{}]", execRes);

                    if (execRes) {
                        // kill all app
                        agentEvalOperateService.stopAllApp(jobInstanceDo, adbcmdReq);
                        break;
                    }

                    // send websocket
                    WebSocketSession session = webSocketSessionManager.getSession(instanceId);
                    log.info("session: {}", session);
                    if (session != null) {
                        MessageSendUtils.sendText(session, inferResp);
                        MessageSendUtils.sendText(session, "******当前任务指令：" + Arrays.toString(taskInfo.getDag()) + "******");
                        MessageSendUtils.sendText(session, "******当前运行第" + taskCounter + "条任务，一共" + instanceTaskInfos.size() + "条任务******");
                    } else {
                        log.error("No active session found for instanceId: {}", instanceId);
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
                    }
                }
            }
            JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), JobStatusEnum.SUCCESS.getValue(), curr, LocalDateTime.now(), ""));
        });
    }

    private String retrieveAppVersion(AgentEvalJobInstanceDo jobInstanceDo, ADBCMDReq adbcmdReq, InstanceTaskInfo taskInfo) {
        String app = taskInfo.getApp();
        AppPackNameEnum appPackNameEnum = AppPackNameEnum.fromName(app);
        String adbCommand = "dumpsys package " + appPackNameEnum.getValue() + " | grep versionName";
        adbcmdReq.setCmd(adbCommand);
        CommonResponse versionResp = agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
        String versionStr = String.valueOf(versionResp.getData());
        if (Strings.isNullOrEmpty(versionStr)) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "get app version failed.");
        }
        versionStr = versionStr.trim();
        if (!versionStr.startsWith("versionName=")) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "The input string does not contain version number information");
        }
        return versionStr.substring("versionName=".length()).trim();
    }

    private void copyNfsFile(String filePath, NfsService nfsService, String currDestDir, String lastDestDir, int stepCounter) {
        try {
            nfsService.copyNfsFileToDir(filePath, currDestDir);
            if (stepCounter > 1) {
                nfsService.copyNfsFileToDirAndRename(filePath, lastDestDir, "last_screenshot.png");
            }
        } catch (IOException e) {
            log.error("copy NFS files error: [{}]", e.getMessage());
        }
    }

    private AgentEvalJobInstanceTrackDo buildAgentEvalJobInstanceTrackDo(InstanceTaskInfo taskInfo, AgentEvalJobInstanceDo jobInstanceDo, ADBCMDReq adbcmdReq) {
        InstanceTrackInfo instanceTrackInfo = new InstanceTrackInfo();
        instanceTrackInfo.setScene(taskInfo.getScene());
        instanceTrackInfo.setAppName(taskInfo.getApp());
        instanceTrackInfo.setAppVersion(retrieveAppVersion(jobInstanceDo, adbcmdReq, taskInfo));
        instanceTrackInfo.setDag(taskInfo.getDag());
        instanceTrackInfo.setInstruction(taskInfo.getInstruction());
        AgentEvalJobInstanceTrackDo jobInstanceTrackDo = new AgentEvalJobInstanceTrackDo();
        jobInstanceTrackDo.setId(UuidUtils.genSimpleUuid());
        jobInstanceTrackDo.setJobInstanceId(jobInstanceDo.getId());
        jobInstanceTrackDo.setTrackInfo(JsonUtils.toJsonString(instanceTrackInfo));
        jobInstanceTrackDo.setTrackDetail("");
        agentEvalJobInstanceTrackMapper.insert(jobInstanceTrackDo);
        return jobInstanceTrackDo;
    }

    private boolean executeAction(String rsp, int[] screenSize, AgentEvalJobInstanceDo jobInstanceDo, ADBCMDReq adbcmdReq) {
        /**
         * 执行解析后的动作。
         * :param rsp: 响应字符串，例如 'Action: tap(0.5, 0.3)' 或 'Action: text(0.5, 0.3, "hello")'
         * :param screenSize: 屏幕尺寸，数组形式 [width, height]
         */
        if (rsp.equals("Action: finish()")) {
            return true;
        }

        boolean finishFlag = false;
        if (rsp.contains(", finish()")) {
            rsp = rsp.replace(", finish()", "");
            finishFlag = true;
        }
        String[] parts = rsp.split(":", 2);
        String actionType = parts[0].trim();
        String params = parts[1].trim();
        if (!validateActions(params, jobInstanceDo)) {
            return true;
        }

        // 'Action: tap(0.5, 0.3)' 或 'Action: text(0.5, 0.3, "hello")'
        if (actionType.equals("Action")) {
            if (params.contains("tap")) {
                log.info("tap");
                Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
                Matcher matcher = pattern.matcher(params);
                float x = 0, y = 0;
                if (matcher.find()) {
                    x = Float.parseFloat(matcher.group());
                }
                if (matcher.find()) {
                    y = Float.parseFloat(matcher.group());
                }
                int screenX = (int) (x * screenSize[0]);
                int screenY = (int) (y * screenSize[1]);
                String command = "input tap " + screenX + " " + screenY;
                log.info("command:" + command);
                // controller.tap(screenX, screenY);
                adbcmdReq.setCmd(command);
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.equals("long_press")) {
                log.info("long_press");
                Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
                Matcher matcher = pattern.matcher(params);
                double x = 0, y = 0;
                if (matcher.find()) {
                    x = Double.parseDouble(matcher.group());
                }
                if (matcher.find()) {
                    y = Double.parseDouble(matcher.group());
                }
                int xCoord = (int) (x * screenSize[0]);
                int yCoord = (int) (y * screenSize[1]);
                adbcmdReq.setCmd(String.format("input swipe %d %d %d %d %d", xCoord, yCoord, xCoord, yCoord, 1000));
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.contains("text")) {
                log.info("text:{}", params);
                String values = params.split("\\(", 2)[1];
                String commandText = null;
                String inputStr;
                if (values.contains(",")) {
                    String[] partsVal = values.substring(0, values.length() - 1).split(",\\s*(?![^()]*\\))");
                    double x = Double.parseDouble(partsVal[0]);
                    double y = Double.parseDouble(partsVal[1]);
                    int xCoord = (int) (x * screenSize[0]);
                    int yCoord = (int) (y * screenSize[1]);
                    if (partsVal.length == 3) {
                        String text = partsVal[2].replace("'", "");
                        String commandTap = String.format("input tap %d %d", xCoord, yCoord);
                        adbcmdReq.setCmd(commandTap);
                        agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                        inputStr = text.replace("\"", "").replace(" ", "%s").replace("'", "");
                        commandText = String.format("input text %s", inputStr);
                    } else {
                        String commandTap = String.format("input tap %d %d", xCoord, yCoord);
                        adbcmdReq.setCmd(commandTap);
                        agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                    }
                } else {
                    String text = values.substring(0, values.length() - 1).replace("'", "").trim();
                    inputStr = text.replace("\"", "");
                    commandText = String.format("input text %s", inputStr);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!Strings.isNullOrEmpty(commandText)) {
                    adbcmdReq.setCmd(commandText);
                    agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                }
            } else if (params.contains("terminate")) {
                log.info("terminate");
                finishFlag = true;
            } else if (params.contains("wait")) {
                log.info("wait");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (params.contains("enter")) {
                log.info("enter");
                adbcmdReq.setCmd(ADBCommand.ENTER.getValue());
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.contains("navigate_back")) {
                log.info("navigate_back");
                adbcmdReq.setCmd(ADBCommand.NAVIGATE_BACK.getValue());
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.contains("navigate_home")) {
                log.info("navigate_home");
                adbcmdReq.setCmd(ADBCommand.NAVIGATE_HOME.getValue());
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.contains("swipe")) {
                log.info("swipe");
                String values = params.split("\\(", 2)[1];
                values = values.substring(0, values.length() - 1);
                String[] coords = values.split(", ");
                int x1 = (int) (Float.parseFloat(coords[0]) * screenSize[0]);
                int y1 = (int) (Float.parseFloat(coords[1]) * screenSize[1]);
                int x2 = (int) (Float.parseFloat(coords[2]) * screenSize[0]);
                int y2 = (int) (Float.parseFloat(coords[3]) * screenSize[1]);
                adbcmdReq.setCmd(String.format("input swipe %d %d %d %d %d", x1, y1, x2, y2, 100));
                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.contains("scroll")) {
                log.info("scroll");
                String values = params.split("\\(", 2)[1];
                values = values.substring(0, values.length() - 1);
                if (values.contains(",")) {
                    String[] partsSplit = values.split(",\\s*(?![^()]*\\))");
                    int x = (int) (Float.parseFloat(partsSplit[0]) * screenSize[0]);
                    int y = (int) (Float.parseFloat(partsSplit[1]) * screenSize[1]);
                    String text = extractText(params);
                    String adbCmd = swipe(x, y, text, "medium", false, screenSize);
                    adbcmdReq.setCmd(adbCmd);
                    agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                } else {
                    String text = extractText(params);
                    int x = (int) (0.5 * screenSize[0]);
                    int y = (int) (0.5 * screenSize[1]);
                    String adbCmd = swipe(x, y, text, "long", false, screenSize);
                    adbcmdReq.setCmd(adbCmd);
                    agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                }
            } else if (params.contains("call_api")) {
                log.info("call_api");
                Pattern pattern = Pattern.compile("\"(.*?)\"");
                Matcher matcher = pattern.matcher(params);
                String[] text = new String[2];
                int index = 0;
                while (matcher.find() && index < 2) {
                    text[index] = matcher.group(1);
                    index++;
                }
                if (text[0] != null) {
                    switch (text[0]) {
                        case "文案生成":
                        case "信息抽取":
                            finishFlag = true;
                            break;
                        case "启动":
                            if (text[1] != null) {
                                String apiName = text[0] + text[1];
                                adbcmdReq.setCmd(StartAppCommand.fromName(apiName).getValue());
                                agentEvalOperateService.execADBCmdServer(adbcmdReq, jobInstanceDo);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        return finishFlag;
    }

    private String extractText(String params) {
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(params);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String swipe(int x, int y, String direction, String dist, boolean quick, int[] screenSize) {
        int unitDist = screenSize[0] / 10;
        switch (dist) {
            case "long":
                unitDist *= 3;
                break;
            case "medium":
                unitDist *= 2;
                break;
            case "very_long":
                unitDist *= 7;
                break;
        }
        int offsetX = 0;
        int offsetY = 0;
        switch (direction) {
            case "up":
                offsetY = -2 * unitDist;
                break;
            case "down":
                offsetY = 2 * unitDist;
                break;
            case "left":
                offsetX = -1 * unitDist;
                break;
            case "right":
                offsetX = unitDist;
                break;
            default:
                break;
        }

        int duration = quick ? 100 : 400;
        return String.format("input swipe %d %d %d %d %d", x, y, x + offsetX, y + offsetY, duration);
    }

    private static boolean validateActions(String actionStr, AgentEvalJobInstanceDo jobInstanceDo) {
        Pattern pattern = Pattern.compile("\\w+\\(.*?\\)");
        Matcher matcher = pattern.matcher(actionStr);

        int actionCount = 0;
        while (matcher.find()) {
            actionCount++;
        }
        if (actionCount > 1) {
            JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), JobStatusEnum.FAILED.getValue(), null, LocalDateTime.now(), "too many actions."));
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "too many actions.");
        }
        return true;
    }

    private AgentEvalJobInstanceDo getAgentEvalJobInstanceDo(AgentEvalJobDetail jobDetail, DeviceInfo device, int curr) {
        OperatorUtils.setContext(jobDetail.getTenantId(), jobDetail.getProjectId(), jobDetail.getCreateUser(), "");
        AgentEvalJobInstanceDo jobInstanceDo = new AgentEvalJobInstanceDo();
        jobInstanceDo.setId(UuidUtils.genSimpleUuid());
        jobInstanceDo.setJobId(jobDetail.getId());
        jobInstanceDo.setJobStatus(JobStatusEnum.PENDING.getValue());
        jobInstanceDo.setJobId(jobDetail.getId());
        jobInstanceDo.setCurrentTurn(curr);
        jobInstanceDo.setDeviceId(device.getDeviceUdid());
        instanceMapper.insert(jobInstanceDo);
        return jobInstanceDo;
    }

    private String buildImgPath(TenantNasInfo tenantNasInfo, String tenantCode, String jobId, String instanceId, String trackId, int stepCounter, String fileName) {
        return FilePathUtils.buildFilePath(Constants.IMG_ROOT_PATH, tenantNasInfo.getNasDir(), tenantCode, jobId, instanceId, trackId,
                "step-".concat(String.valueOf(stepCounter)).concat(Constants.WORKING_DIR).concat(fileName));
    }

    private String buildInferResponse(String inferResp, int stepCounter, String imgPathBefore, String imgPathAfter) {
        return new StringBuilder()
                .append(inferResp)
                .append(".\n\nStep: ")
                .append(stepCounter)
                .append("\n\nImgPathBefore: ")
                .append(imgPathBefore)
                .append("\n\nImgPathAfter: ")
                .append(imgPathAfter)
                .toString();
    }

    private static void saveImage(InputStream inputStream, String destinationFile) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(destinationFile));
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String replaceParams(String instruction, JSONObject paramJson) {
        if (paramJson == null || paramJson.isEmpty()) {
            return instruction;
        }
        for (String key : paramJson.keySet()) {
            String placeholder = "{" + key + "}";
            if (instruction.contains(placeholder)) {
                instruction = instruction.replace(placeholder, paramJson.getString(key));
            }
        }
        return instruction;
    }

    public String checkPrompt(AgentEvalJobDetail jobDetail, InstanceTaskInfo taskInfo, int stepCounter) {
        String promptTemplate = jobDetail.getPromptTemplate();
        String resultPrompt;
        boolean containsStepInstruction = promptTemplate.contains("<step_instruction>");
        boolean containsTaskInstruction = promptTemplate.contains("<task_instruction>");
        if (containsStepInstruction && containsTaskInstruction) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "Error: The input contains both <step_instruction> and <task_instruction>. Only one is allowed.");
        } else if (containsStepInstruction) {
            resultPrompt = promptTemplate.replace("<step_instruction>", taskInfo.getDag()[stepCounter]);
        } else if (containsTaskInstruction) {
            JSONObject paramJson = JsonUtils.parseObject(jobDetail.getParams(), JSONObject.class);
            resultPrompt = promptTemplate.replace("<task_instruction>", replaceParams(taskInfo.getInstruction(), paramJson));
        } else {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "No relevant instructions found.");
        }
        return resultPrompt;
    }

    private void drawOnImageByAction(String filePath, String destPath, String action, int[] screenSizeArr) {
        log.info("filePath: {}, action:{}, screenSizeArr:{}", filePath, action, screenSizeArr);
        String[] parts = action.split(":", 2);
        String specAction = parts[1].trim();
        if (specAction.contains("tap") || specAction.contains("long_press") || specAction.contains("text") || specAction.contains("scroll")) {
            double[] coordinates = DrawUtils.extractCoordinates(specAction);
            log.info("coordinates: {}", coordinates);
            double x = coordinates[0];
            double y = coordinates[1];
            x = x * screenSizeArr[0];
            y = y * screenSizeArr[1];
            DrawUtils.drawOnImage(filePath, Constants.SCREENSHOT_PNG, destPath, new double[]{x, y});
        }
    }

    private void compressImg(String filePath) {
        try {
            PngCompressor.compress(new File(filePath), new File(filePath));
            log.info("compress img success.");
        } catch (IOException e) {
            throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
        }
    }
}