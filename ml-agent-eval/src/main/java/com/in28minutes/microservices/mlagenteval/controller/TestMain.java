package com.in28minutes.microservices.mlagenteval.controller;

import com.alibaba.fastjson.JSONObject;
import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.dto.ADBCMDReq;
import com.in28minutes.microservices.mlagenteval.utils.HttpUtil;
import com.in28minutes.microservices.mlagenteval.utils.JsonUtils;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMain {
    public static void main(String[] args) {
        String imagePath = "D:\\work\\agent-simulation\\step_xx.PNG";


        // request to get screen size.
        ADBCMDReq adbcmdReq = new ADBCMDReq();
        // adbcmdReq.setUdId("AHKS012A27000340");
        adbcmdReq.setUdId("MQS0219919010300");
        adbcmdReq.setCmd("wm size");
        /*CommonResponse resp = execADBCmdServer(adbcmdReq);
        String screenSize = String.valueOf(resp.getData());
        if (StringUtils.isEmpty(screenSize)) {
            // throw new BusinessException(BizErrorCode.SERVER_ERROR, "obtain screen size failed, pls check device.");
            throw new RuntimeException("obtain screen size failed, pls check device.");
        }*/

        // make the prompt
        String text = "You are an agent trained to perform some basic tasks on a smartphone. You will be given a smartphone screenshot.  \\n\\nYou can call the following functions to control the smartphone:  \\n\\n1. tap(x: float, y: float)  \\n   This function is used to tap a specific point on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen.  \\n\\n2. text(x: float, y: float, text_input: str)  \\n   This function is used to insert text input at a specific location on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen, and text_input is the string you want to insert.  \\n\\n3. scroll(x: float, y: float, direction: str)  \\n   This function is used to scroll on the smartphone screen from the starting coordinate (x, y) in the specified direction. The coordinates x and y are normalized values between 0 and 1 representing the starting position on the screen. The direction can be one of \"up\", \"down\", \"left\", or \"right\".  \\n\\n4. long_press(x: float, y: float)  \\n   This function is used to perform a long press at a specific point on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen.  \\n\\n5. call_api(api_name: str, params: str)  \\n   This function is used to call a specified API interface. The parameter `api_name` is a string that describes the name of the API to be called, and `params` is a string containing the relevant input parameters required by the API.  \\n\\n6. navigate_home()  \\n   This function is used to navigate to the home screen of the smartphone.  \\n\\n7. navigate_back()  \\n   This function is used to navigate back to the previous screen on the smartphone.  \\n\\n8. wait(message: Optional[str] = \"\")  \\n   This function is used to wait for a certain period before performing the next action. An optional message can be provided to inform the user why waiting is necessary.  \\n\\n9. enter()  \\n   This function is used to simulate pressing the enter key on the smartphone.  \\n\\n10. finish(message: Optional[str] = \"\")  \\n    This function indicates that the current task is completed and no further actions are needed. An optional string message can be provided for user feedback.  \\n\\n11. take_over(message: Optional[str] = \"\")  \\n    This function is used when the agent determines that the user needs to manually decide or intervene to proceed further. An optional string message can be provided to guide the user.  \\n\\n12. terminate(message: Optional[str] = \"\")  \\n    This function is used to indicate an unexpected termination of the task. An optional string message can be provided to inform the user about the reason for termination.  \\n\\nThe current task instruction is to perform 打开支付宝的余额宝菜单.\\n\\nThese are the key steps to complete the task:\\n1. 我应该在支付宝主页上找到“余额宝”选项并点击它。\\n2. 点击当前页面中部区域的“余额宝”选项。\\n3. 点击当前页面上的“余额宝”选项。\\n4. 不需要任何交互，因为余额宝菜单已经打开。\\n\\n\\nYou can refer to the above key steps, but the action should be flexibly adjusted based on the present screenshot.   \\n\\nNow, given the following screenshot, you need to think and call the function needed to proceed with the task. Your output should be in the given format:  \\nObservation: <Describe what you observe in the image>  \\nThought: <To complete the given task, what is the next step I should do>  \\nAction: <Use the function call with the correct parameters to proceed with the task. If you believe the task is completed or there is nothing to be done, you should call the finish() function. If you believe executing an action will complete the task, add \", finish()\" at the end of the action. You cannot output anything else except a function call in this field.>  \\n\\nYou can only perform one action at a time, so you can only call one function.";

        int stepCounter = 0;
        String response = "";
        while (true) {
            // obtain current screenshot, and upload the img to the nas, replace the last screenshot
            adbcmdReq.setCmd("screencap -p /sdcard/screenshot.png");
            // execADBCmdServer(adbcmdReq);
            adbcmdReq.setCmd("pull /sdcard/screenshot.png ./screenshot");
            // execADBCmdServer(adbcmdReq);

            stepCounter++;
            // start the inference
            /*String urlString = "http://10.162.199.90:31000/inference2";
            JSONObject inferResp = inference(imagePath, text, urlString);
            String response = (String) inferResp.get("response");
            if (Objects.isNull(response)) {
                throw new RuntimeException("model inference failed.");
            }*/
            if (stepCounter == 1) {
                response = "Observation: The screenshot shows the main page of Alipay. There is a \"余额宝\" option located at the bottom right corner of the screen.\nThought: To open the balance treasure menu, I should tap on the \"余额宝\" option at the bottom right corner of the screen.\nAction: tap(0.9, 0.9)";
            } else if (stepCounter == 2) {
                response = "Observation: The screenshot shows the main page of Alipay. There is a \"余额宝\" option located at the bottom right corner of the screen.\nThought: To open the balance treasure menu, I should tap on the \"余额宝\" option at the bottom right corner of the screen.\nAction: tap(0.9, 0.9), finish()";
            }
            String specAction;
            int actionStart = response.indexOf("Action:");
            if (actionStart != -1) {
                specAction = response.substring(actionStart).replace("\n", "").replace("\"}", "");
            } else {
                throw new RuntimeException("inference response has no action.");
            }
            System.out.println("specAction:" + specAction);

            // Execute parsed actions
            // int[] screenSizeArr = parseScreenSize(screenSize);
            int[] screenSizeArr = {1080, 2340};
            ; // 示例屏幕尺寸
            boolean execRes = executeAction(specAction, screenSizeArr);
            System.out.println("=========================== step: " + stepCounter + " exec finished =======================");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Action execRes: " + execRes);
            if (execRes) {
                // kill all app
                break;
            }
        }
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

    public static boolean executeAction(String rsp, int[] screenSize) {
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
                // execADBCmdServer(adbcmdReq);
            }

        }
        return finishFlag;
    }


    private static JSONObject inference(String imagePath, String text, String urlString) {
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
                String response = readInputStream(connection.getInputStream());
                String resp = decodeUnicodeString(response);
                return JsonUtils.parseObject(resp, JSONObject.class);
            } else {
                System.out.println("POST request failed with response code: " + responseCode);

            }
        } catch (Exception e) {
            // throw new BusinessException(BizErrorCode.SERVER_ERROR, "call inference api failed.");
            throw new RuntimeException("call inference api failed.");
        }
        return null;
    }

    @NotNull
    private static CommonResponse execADBCmdServer(ADBCMDReq adbcmdReq) {
        // return HttpUtil.post("http://10.162.168.141:8888/devices/execADBCommand", adbcmdReq, CommonResponse.class);
        return HttpUtil.post("http://192.168.31.115:8888/devices/execADBCommand", adbcmdReq, CommonResponse.class);
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    private static String decodeUnicodeString(String unicodeString) {
        StringBuilder decodedString = new StringBuilder();
        int length = unicodeString.length();
        for (int i = 0; i < length; i++) {
            char c = unicodeString.charAt(i);
            if (c == '\\' && i + 1 < length && unicodeString.charAt(i + 1) == 'u') {
                int codePoint = Integer.parseInt(unicodeString.substring(i + 2, i + 6), 16);
                decodedString.append((char) codePoint);
                i += 5;
            } else {
                decodedString.append(c);
            }
        }
        return decodedString.toString();
    }
}
