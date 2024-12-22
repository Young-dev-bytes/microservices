package com.in28minutes.microservices.mlagenteval.common.event.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.eventbus.Subscribe;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobExecEvent;
import com.in28minutes.microservices.mlagenteval.common.event.JobStatusEvent;
import com.in28minutes.microservices.mlagenteval.common.event.JobTrackDetailEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceTrackDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceTrackMapper;
import com.in28minutes.microservices.mlagenteval.dto.*;
import com.in28minutes.microservices.mlagenteval.enums.ADBCommand;
import com.in28minutes.microservices.mlagenteval.enums.BizErrorCode;
import com.in28minutes.microservices.mlagenteval.exception.BusinessException;
import com.in28minutes.microservices.mlagenteval.service.AgentOperationService;
import com.in28minutes.microservices.mlagenteval.utils.ADBUtils;
import com.in28minutes.microservices.mlagenteval.utils.FilePathUtils;
import com.in28minutes.microservices.mlagenteval.utils.JsonUtils;
import com.in28minutes.microservices.mlagenteval.utils.UuidUtils;
import com.in28minutes.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import com.in28minutes.microservices.mlagenteval.websockets.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
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
    public static final String NFS_SERVER = "10.69.179.245";
    public static final String EXPORTED_PATH = "/agent";
    public static final String IMG_ROOT_PATH = "/agent_eval/ui_agent";
    public static final String SCREENSHOT_PNG = "screenshot.png";
    public static final String CACHE = "cache";
    // AgentConfig agentConfig = SpringBeanUtils.getBean(AgentConfig.class);
    AgentEvalJobInstanceTrackMapper agentEvalJobInstanceTrackMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceTrackMapper.class);
    WebSocketSessionManager webSocketSessionManager = SpringBeanUtils.getBean(WebSocketSessionManager.class);
    AgentOperationService agentOperationService = SpringBeanUtils.getBean(AgentOperationService.class);


    @Subscribe
    public void handle(JobExecEvent jobExecEvent) throws IOException {
        List<InstanceTaskInfo> instanceTaskInfos = jobExecEvent.getJobDetail().getInstanceTaskInfos();
        if (CollectionUtils.isEmpty(instanceTaskInfos)) {
            log.info("no job dataset executable....");
            return;
        }

        AgentEvalJobInstanceMapper instanceMapper = SpringBeanUtils.getBean(AgentEvalJobInstanceMapper.class);
        // NfsService nfsService = new NfsService(NFS_SERVER, EXPORTED_PATH);
        AgentEvalJobDetail jobDetail = jobExecEvent.getJobDetail();
        DeviceInfo device = jobDetail.getDeviceInfoList().get(0);
        String udId = device.getDeviceUdid();
        String promptTemplate = jobDetail.getPromptTemplate();

        // execute turn, every turn will generate an instance
        Integer executeTurn = jobDetail.getExecuteTurn();
        IntStream.rangeClosed(1, executeTurn).forEach(curr -> {
            log.info("current execute turn: [{}]", curr);
            AgentEvalJobInstanceDo jobInstanceDo = buildAgentEvalJobInstanceDo(instanceMapper, jobDetail, device, curr);
            String instanceId = jobInstanceDo.getId();
            String jobId = jobInstanceDo.getJobId();
            log.info("jobStatusEvent: instanceId[{}]", instanceId);

            JobEventRegisterCenter.post(new JobStatusEvent(instanceId, "RUNNING", curr, ""));

            // request to get screen size.
            ADBCMDReq adbcmdReq = new ADBCMDReq();
            adbcmdReq.setUdId(udId);
            adbcmdReq.setCmd(ADBCommand.WM_SIZE.getValue());
            /*CommonResponse resp = ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);
            String screenSize = String.valueOf(resp.getData());
            if (StringUtils.isEmpty(screenSize)) {
                throw new BusinessException(BizErrorCode.SERVER_ERROR, "obtain screen size failed, pls check device.");
            }*/

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
            }



            Iterator<InstanceTaskInfo> iterator = instanceTaskInfos.iterator();
            while (iterator.hasNext()) {
                InstanceTaskInfo taskInfo = iterator.next();

                String taskInstruction = taskInfo.getInstruction();
                String dag = taskInfo.getDag();

                AgentEvalJobInstanceTrackDo jobInstanceTrackDo = buildAgentEvalJobInstanceTrackDo(taskInfo, instanceId);

                String prompt = promptTemplate.replace("<task_instruction>", taskInstruction)
                        .replace("<dag>", dag);
                int stepCounter = 0;
                List<String> inferList = new ArrayList<>();

                while (true) {
                    stepCounter++;
                    // obtain current screenshot, and upload the img to the nas, replace the last screenshot
                    // then upload the nas dir:
                    /*adbcmdReq.setCmd(ADBCommand.SCREENSHOT.getCommand());
                    ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);*/

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
                    }

                    adbcmdReq.setCmd(ADBCommand.PULL_SCREENSHOT.getValue());
                    adbcmdReq.setScreenshotPath(FilePathUtils.buildFilePath(jobId, instanceId, jobInstanceTrackDo.getId(), CACHE));
                    // ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);

                    log.info("current step is: [{}]", stepCounter);
                    // start model inference by calling model
                    String filePath = FilePathUtils.buildFilePath(EXPORTED_PATH, jobId, instanceId,
                            jobInstanceTrackDo.getId(), CACHE, SCREENSHOT_PNG);

                    /*InputStream inputStreamLoad = nfsService.downloadNfsFile(filePath);
                    try {
                        saveImage(inputStreamLoad, SCREENSHOT_PNG);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }*/
                    String destDir = FilePathUtils.buildFilePath(EXPORTED_PATH, jobId, instanceId,
                            jobInstanceTrackDo.getId(), "step_".concat(String.valueOf(stepCounter)));
                    /*try {

                        nfsService.copyNfsFileToDir(filePath, destDir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }*/

                    // InputStream inputStream = nfsService.downloadNfsFile(filePath);
                    //String inferResp = ADBUtils.execInference(inputStream, prompt, "agentConfig.getUrlInference()," , jobInstanceDo.getId());
                    Session session = webSocketSessionManager.getSession(instanceId);
                    String inferResp = "";
                    if (stepCounter == 1) {
                        inferResp = "Observation: 在提供的支付宝应用截图中，我看到了主界面的布局。在屏幕的中部位置，有一个明显的“余额宝”图标或文字链接，这正是我们需要点击来打开余额宝菜单的元素。\n" +
                                "\n" +
                                "Thought: 根据观察，下一步应该是直接点击“余额宝”选项以打开余额宝菜单。由于“余额宝”位于屏幕中部，我们可以使用 tap 函数并提供适当的坐标来模拟点击操作。假设“余额宝”的中心点大约位于屏幕宽度的中间 (0.5) 和高度的大约三分之一处 (0.33)，这是个常见的布局位置。\n" +
                                "\n" +
                                "Action: tap(0.5, 0.33)";
                        if (session != null && session.isOpen()) {
                            agentOperationService.sendText(session, inferResp);
                        } else {
                            log.warn("No active session found for instanceId: {}", instanceId);
                        }
                    } else if (stepCounter == 2) {
                        inferResp = "Observation: 在提供的支付宝应用截图中，我可以看到主界面布局。在屏幕的中部位置，有一个明显的“余额宝”图标或文字链接，这正是我们需要点击来打开余额宝菜单的元素。\n" +
                                "\n" +
                                "Thought: 为了完成任务，下一步应该是在屏幕上找到并点击“余额宝”选项。由于我无法直接看到具体的坐标值，我将基于一般的设计原则进行估计。“余额宝”通常位于支付宝首页的中部偏上位置。如果这个点击成功，余额宝菜单将会打开，那么任务就完成了。\n" +
                                "\n" +
                                "Action: tap(0.5, 0.3), finish()";

                        if (session != null && session.isOpen()) {
                            agentOperationService.sendText(session, inferResp);
                        } else {
                            log.warn("No active session found for instanceId: {}", instanceId);
                        }
                    }

                    log.info("inferResp:[{}]", inferResp);
                    if (Objects.isNull(inferResp)) {
                        JobEventRegisterCenter.post(new JobStatusEvent(instanceId, "FAILED", curr, "model inference failed."));
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, "model inference failed.");
                    }
                    String imgPath = FilePathUtils.buildFilePath(IMG_ROOT_PATH, jobId, instanceId,
                            jobInstanceTrackDo.getId(), "step_".concat(String.valueOf(stepCounter)));

                    inferList.add(inferResp.concat(".\n\nStep: ".concat(String.valueOf(stepCounter))
                            .concat("\n\nImgPath: ").concat(imgPath).concat("/").concat(SCREENSHOT_PNG)));

                    JobEventRegisterCenter.post(new JobTrackDetailEvent(jobInstanceTrackDo.getId(), inferList, String.valueOf(stepCounter)));

                    String specAction = ADBUtils.obtainSpecAction(inferResp);
                    // Execute parsed actions; int[] screenSizeArr = {1080, 2340}; // 示例屏幕尺寸
                    // int[] screenSizeArr = ADBUtils.parseScreenSize(screenSize);
                    int[] screenSizeArr = {1080, 2340};
                    log.info("specAction:[{}]", specAction);
                    boolean execRes = executeAction(specAction, screenSizeArr, jobInstanceDo, adbcmdReq);
                    log.info("=========================== step: " + stepCounter + " exec finished =======================");

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
                    }
                    log.info("Action execRes:[{}]", execRes);
                    if (execRes) {
                        // kill all app
                        break;
                    }
                }
            }
            JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), "SUCCESS", curr, ""));
        });
    }

    private AgentEvalJobInstanceTrackDo buildAgentEvalJobInstanceTrackDo(InstanceTaskInfo taskInfo, String instanceId) {
        InstanceTrackInfo instanceTrackInfo = new InstanceTrackInfo();
        instanceTrackInfo.setScene(taskInfo.getScene());
        instanceTrackInfo.setAppName(taskInfo.getApp());
        instanceTrackInfo.setAppVersion("");
        instanceTrackInfo.setDag(taskInfo.getDag());
        instanceTrackInfo.setTargetStepNum(taskInfo.getStepNum());
        instanceTrackInfo.setInstruction(taskInfo.getInstruction());

        AgentEvalJobInstanceTrackDo jobInstanceTrackDo = new AgentEvalJobInstanceTrackDo();
        jobInstanceTrackDo.setId(UuidUtils.genSimpleUuid());
        jobInstanceTrackDo.setJobInstanceId(instanceId);
        jobInstanceTrackDo.setTrackInfo(JsonUtils.toJsonString(instanceTrackInfo));
        jobInstanceTrackDo.setTrackDetail("");
        jobInstanceTrackDo.setInstructionId("xxx");
        jobInstanceTrackDo.setCreateTime(LocalDateTime.now());
        agentEvalJobInstanceTrackMapper.insert(jobInstanceTrackDo);
        return jobInstanceTrackDo;
    }

    private boolean executeAction(String rsp, int[] screenSize, AgentEvalJobInstanceDo jobInstanceDo, ADBCMDReq adbcmdReq) {
        /*
         * 执行解析后的动作。
         * :param rsp: 响应字符串，例如 'Action: tap(0.5, 0.3)' 或 'Action: text(0.5, 0.3, "hello")'
         * :param screenSize: 屏幕尺寸，数组形式 [width, height]
         */
        Map<String, String> apiDict = new HashMap<>();
        apiDict.put("启动微信", "adb shell am force-stop com.tencent.mm && adb shell am start -n com.tencent.mm/.ui.LauncherUI");
        apiDict.put("启动设置", "adb shell am force-stop com.android.settings && adb shell am start -a android.settings.SETTINGS");
        apiDict.put("启动滴滴", "adb shell am force-stop com.sdu.didi.psnger && adb shell monkey -p com.sdu.didi.psnger -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动高德地图", "adb shell am force-stop com.autonavi.minimap && adb shell monkey -p com.autonavi.minimap -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动顺丰", "adb shell am force-stop com.sf.activity && adb shell monkey -p com.sf.activity -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动美团", "adb shell am force-stop com.sankuai.meituan && adb shell monkey -p com.sankuai.meituan -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动去哪儿旅行", "adb shell am force-stop com.Qunar && adb shell monkey -p com.Qunar -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动飞猪旅行", "adb shell am force-stop com.taobao.trip && adb shell monkey -p com.taobao.trip -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动艺龙旅行", "adb shell am force-stop com.dp.android.elong && adb shell monkey -p com.dp.android.elong -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动携程旅行", "adb shell am force-stop ctrip.android.view && adb shell monkey -p ctrip.android.view -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动同程旅行", "adb shell am force-stop com.tongcheng.android && adb shell monkey -p com.tongcheng.android -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动淘宝", "adb shell am force-stop com.taobao.taobao && adb shell monkey -p com.taobao.taobao -c android.intent.category.LAUNCHER 1");
        apiDict.put("启动京东", "adb shell am force-stop com.jingdong.app.mall && adb shell monkey -p com.jingdong.app.mall -c android.intent.category.LAUNCHER 1");
        apiDict.put("截图", "echo \"No operation\"");

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
        // 'Action: tap(0.5, 0.3)' 或 'Action: text(0.5, 0.3, "hello")'
        if (actionType.equals("Action")) {
            if (params.contains("tap")) {
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
                // ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } else if (params.equals("long_press")) {
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
                // longPress(xCoord, yCoord, 1000);
                String command = String.format("input swipe %d %d %d %d %d", xCoord, yCoord, xCoord, yCoord, 1000);
                adbcmdReq.setCmd(command);
                // ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);
            } /*else if (params.contains("text")) {
                String values = params.split("\\(", 2)[1];
                String commandText;
                String inputStr;
                if (values.contains(",")) {
                    String[] partsVal = values.substring(0, values.length() - 1).split(",\\s*(?![^()]*\\))");
                    double x = Double.parseDouble(partsVal[0]);
                    double y = Double.parseDouble(partsVal[1]);
                    String text = partsVal[2].replace("'", "");

                    int xCoord = (int) (x * screenSize[0]);
                    int yCoord = (int) (y * screenSize[1]);

                    String commandTap = String.format("input tap %d %d", xCoord, yCoord);
                    adbcmdReq.setCmd(commandTap);
                    ADBUtils.execADBCmdServer(agentConfig.getUrlExecAdbCmd(), adbcmdReq, jobInstanceDo);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    inputStr = text.replace("\"", "").replace(" ", "%s").replace("'", "");
                } else {
                    String text = values.substring(0, values.length() - 1).replace("'", "").trim();
                    inputStr = text.replace("\"", "");
                }
                commandText = String.format("am broadcast -a ADB_INPUT_TEXT --es msg %s", inputStr);
                adbcmdReq.setCmd(commandText);
                ADBUtils.execADBCmdServer(agentConfig.getUrlExecAdbCmd(), adbcmdReq, jobInstanceDo);
            }*/

        }
        return finishFlag;
    }

    public static void saveImage(InputStream inputStream, String destinationFile) throws IOException {
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

    private AgentEvalJobInstanceDo buildAgentEvalJobInstanceDo(AgentEvalJobInstanceMapper instanceMapper,
                                                               AgentEvalJobDetail jobDetail,
                                                               DeviceInfo device,
                                                               int curr) {
        // OperatorUtils.setContext(jobDetail.getTenantId(), jobDetail.getProjectId(), jobDetail.getCreateUser(), "");
        AgentEvalJobInstanceDo jobInstanceDo = new AgentEvalJobInstanceDo();
        jobInstanceDo.setId(UuidUtils.genSimpleUuid());
        jobInstanceDo.setJobId(jobDetail.getId());
        jobInstanceDo.setJobStatus("CREATED");
        jobInstanceDo.setJobId(jobDetail.getId());
        jobInstanceDo.setCurrentTurn(curr);
        jobInstanceDo.setDeviceId(device.getDeviceUdid());
        jobInstanceDo.setCreateTime(LocalDateTime.now());
        instanceMapper.insert(jobInstanceDo);
        return jobInstanceDo;
    }


}