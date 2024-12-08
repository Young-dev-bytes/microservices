package com.in28minutes.microservices.mlagenteval.utils;

import com.alibaba.fastjson.JSONObject;
import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobStatusEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dto.ADBCMDReq;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ADBUtils {

    public static final String URL_EXEC_ADBCOMMAND = "http://192.168.31.115:8888/devices/execADBCommand";

    public static boolean executeAction(String rsp, int[] screenSize, AgentEvalJobInstanceDo jobInstanceDo) {
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
                System.out.println("command:" + command);

                // controller.tap(screenX, screenY);
                ADBCMDReq adbcmdReq = new ADBCMDReq();
                adbcmdReq.setUdId("AHKS012A27000340");
                adbcmdReq.setCmd(command);
                execADBCmdServer(adbcmdReq, jobInstanceDo);
            }

        }
        return finishFlag;
    }


    public static String execInference(String imagePath, String text, String urlString) {
        try {
            File imageFile = new File(imagePath);
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            byte[] imageBytes = new byte[(int) imageFile.length()];
            fileInputStream.read(imageBytes);
            fileInputStream.close();

            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write(("Content-Disposition: form-data; name=\"text\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write((text + "\r\n").getBytes(StandardCharsets.UTF_8));

            outputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write(("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write(("Content-Type: image/png\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = UseStreamUtils.readInputStream(connection.getInputStream());
                String resp = UnicodeUtils.decodeUnicodeString(response);
                return JsonUtils.parseObject(resp, JSONObject.class).getString("response");

            } else {
                System.out.println("POST request failed with response code: " + responseCode);

            }
        } catch (Exception e) {
            // throw new BusinessException(BizErrorCode.SERVER_ERROR, "call inference api failed.");
            throw new RuntimeException("call inference api failed.");
        }
        return null;
    }

    public static CommonResponse execADBCmdServer(ADBCMDReq adbcmdReq, AgentEvalJobInstanceDo jobInstanceDo) {
        // return HttpUtil.post("http://10.162.168.141:8888/devices/execADBCommand", adbcmdReq, CommonResponse.class);
        CommonResponse response = HttpUtil.post(URL_EXEC_ADBCOMMAND, adbcmdReq, CommonResponse.class);
        if (!response.getSuccess()) {
            JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), "FAILED"));
        }
        return response;
    }


    public static int[] parseScreenSize(String screenSizeStr) {
        Pattern pattern = Pattern.compile("Physical size: (\\d+)x(\\d+)");
        Matcher matcher = pattern.matcher(screenSizeStr);
        int[] screenSize = new int[2];
        if (matcher.find()) {
            screenSize[0] = Integer.parseInt(matcher.group(1));
            screenSize[1] = Integer.parseInt(matcher.group(2));
        }
        return screenSize;
    }


    public static String obtainSpecAction(String response) {
        String specAction;
        int actionStart = response.indexOf("Action:");
        if (actionStart != -1) {
            specAction = response.substring(actionStart).replace("\n", "").replace("\"}", "");
        } else {
            throw new RuntimeException("inference response has no action.");
        }
        log.info("specAction:" + specAction);
        return specAction;
    }
}
