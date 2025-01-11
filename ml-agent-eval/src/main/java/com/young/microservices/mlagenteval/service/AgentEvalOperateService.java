package com.young.microservices.mlagenteval.service;

import com.alibaba.fastjson.JSONObject;
import com.young.microservices.mlagenteval.common.CommonResponse;
import com.young.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.young.microservices.mlagenteval.common.event.JobStatusEvent;
import com.young.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.young.microservices.mlagenteval.dto.ADBCMDReq;
import com.young.microservices.mlagenteval.enums.BizErrorCode;
import com.young.microservices.mlagenteval.enums.JobStatusEnum;
import com.young.microservices.mlagenteval.enums.StopAppCommand;
import com.young.microservices.mlagenteval.exception.BusinessException;
import com.young.microservices.mlagenteval.utils.JsonUtils;
import com.young.microservices.mlagenteval.utils.UnicodeUtils;
import com.young.microservices.mlagenteval.utils.UseStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 功能描述:
 *
 * @author cw0106718
 * @since 2024-11-25
 */
@Slf4j
@Service
public class AgentEvalOperateService {

    public String execInference(InputStream inputStream, String text, String instanceId) {
        // String inferenceUrl = ConfigCenter.getInstance().getConfig("url.inference", "");
        String inferenceUrl = "";
        String boundary = "----WebKitFormBoundary" + Long.toHexString(System.currentTimeMillis());
        try {
            byte[] imageBytes = inputStreamToByteArray(inputStream);
            URL url = new URL(inferenceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"text\"\r\n\r\n");
            writer.append(text).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"screenshot.png\"\r\n");
            writer.append("Content-Type: image/png\r\n\r\n");
            writer.flush();
            outputStream.write(imageBytes);
            outputStream.flush();
            writer.append("\r\n--").append(boundary).append("--\r\n");
            writer.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = UseStreamUtils.readInputStream(connection.getInputStream());
                String resp = UnicodeUtils.decodeUnicodeString(response);
                return JsonUtils.parseObject(resp, JSONObject.class).getString("response");
            } else {
                String errorResponse = UseStreamUtils.readInputStream(connection.getErrorStream());
                log.error("POST request failed with response code: " + responseCode + ", error response: " + errorResponse);
            }
        } catch (Exception e) {
            log.error("Exception occurred while calling inference API", e);
            JobEventRegisterCenter.post(new JobStatusEvent(instanceId, JobStatusEnum.FAILED.getValue(), null, LocalDateTime.now(), "call inference api failed."));
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "call inference api failed.");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public CommonResponse execADBCmdServer(ADBCMDReq adbcmdReq, AgentEvalJobInstanceDo jobInstanceDo) {
        // String adbServeUrl = ConfigCenter.getInstance().getConfig("url.exec.adb", "");
        String adbServeUrl = "";
        try {
            // CommonResponse response = InvokeUtil.post(adbServeUrl, adbcmdReq, CommonResponse.class);
            CommonResponse response = new CommonResponse();
            if (response.getCode() != 200) {
                log.error("execADBCmdServer failed! pls check the cmd: :[{}]", adbcmdReq.getCmd());
                JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), JobStatusEnum.FAILED.getValue(), null, LocalDateTime.now(), "call adb api failed! pls check the api."));
            }
            return response;
        } catch (Exception e) {
            log.error("exec adb cmd failed:[{}]", e.getMessage());
            JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), JobStatusEnum.FAILED.getValue(), null, LocalDateTime.now(), "call adb api failed! pls check the api."));
            throw new BusinessException(BizErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    public int[] parseScreenSize(String screenSizeStr) {
        Pattern pattern = Pattern.compile("Physical size: (\\d+)x(\\d+)");
        Matcher matcher = pattern.matcher(screenSizeStr);
        int[] screenSize = new int[2];
        if (matcher.find()) {
            screenSize[0] = Integer.parseInt(matcher.group(1));
            screenSize[1] = Integer.parseInt(matcher.group(2));
        }
        return screenSize;
    }

    public String obtainSpecAction(String response, String instanceId) {
        String specAction;
        int actionStart = response.indexOf("Action:");
        if (actionStart != -1) {
            specAction = response.substring(actionStart).replace("\n", "").replace("\"}", "");
        } else {
            JobEventRegisterCenter.post(new JobStatusEvent(instanceId, JobStatusEnum.FAILED.getValue(), null, LocalDateTime.now(), "inference response has no action."));
            throw new BusinessException(BizErrorCode.SERVER_ERROR, "inference response has no action.");
        }
        return specAction;
    }

    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void stopAllApp(AgentEvalJobInstanceDo jobInstanceDo, ADBCMDReq adbcmdReq) {
        List<String> allStopCmd = StopAppCommand.getAllStopCmd();
        for (String cmd : allStopCmd) {
            adbcmdReq.setCmd(cmd);
            execADBCmdServer(adbcmdReq, jobInstanceDo);
        }
    }
}
