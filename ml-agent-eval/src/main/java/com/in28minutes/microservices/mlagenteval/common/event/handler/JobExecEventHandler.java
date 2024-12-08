package com.in28minutes.microservices.mlagenteval.common.event.handler;

import com.google.common.eventbus.Subscribe;
import com.in28minutes.microservices.mlagenteval.common.CommonResponse;
import com.in28minutes.microservices.mlagenteval.common.event.JobEventRegisterCenter;
import com.in28minutes.microservices.mlagenteval.common.event.JobExecEvent;
import com.in28minutes.microservices.mlagenteval.common.event.JobStatusEvent;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobInstanceDo;
import com.in28minutes.microservices.mlagenteval.dao.mapper.AgentEvalJobInstanceMapper;
import com.in28minutes.microservices.mlagenteval.dto.ADBCMDReq;
import com.in28minutes.microservices.mlagenteval.dto.DeviceInfo;
import com.in28minutes.microservices.mlagenteval.utils.ADBUtils;
import com.in28minutes.microservices.mlagenteval.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * desc:
 *
 * @author Young.
 * @since 2024/5/18 17:32
 */
@Slf4j
public class JobExecEventHandler {

    private final static String URL_INFERENCE = "http://10.162.199.90:31000/inference2";

    @Autowired
    private AgentEvalJobInstanceMapper instanceMapper;

    @Subscribe
    public void handle(JobExecEvent jobExecEvent) {

        String eventJobId = jobExecEvent.getJobId();
        DeviceInfo deviceInfo = jobExecEvent.getDeviceInfo();
        String udId = deviceInfo.getDeviceUdid();

        log.info("jobStatusEvent: instanceId[{}]", eventJobId);
        AgentEvalJobInstanceDo jobInstanceDo = instanceMapper.selectById(eventJobId);
        if (Objects.isNull(jobInstanceDo)) {
            throw new RuntimeException("job not exist!");
        }

        String imagePath = "D:\\work\\agent-simulation\\step_xx.PNG";

        JobEventRegisterCenter.post(new JobStatusEvent(eventJobId, "RUNNING"));

        // request to get screen size.
        ADBCMDReq adbcmdReq = new ADBCMDReq();
        adbcmdReq.setUdId(udId);
        adbcmdReq.setCmd("wm size");
        CommonResponse resp = ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);
        String screenSize = String.valueOf(resp.getData());
        if (StringUtils.isEmpty(screenSize)) {
            // throw new BusinessException(BizErrorCode.SERVER_ERROR, "obtain screen size failed, pls check device.");
            throw new RuntimeException("obtain screen size failed, pls check device.");
        }

        // make the prompt
        String text = "You are an agent trained to perform some basic tasks on a smartphone. You will be given a smartphone screenshot.  \\n\\nYou can call the following functions to control the smartphone:  \\n\\n1. tap(x: float, y: float)  \\n   This function is used to tap a specific point on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen.  \\n\\n2. text(x: float, y: float, text_input: str)  \\n   This function is used to insert text input at a specific location on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen, and text_input is the string you want to insert.  \\n\\n3. scroll(x: float, y: float, direction: str)  \\n   This function is used to scroll on the smartphone screen from the starting coordinate (x, y) in the specified direction. The coordinates x and y are normalized values between 0 and 1 representing the starting position on the screen. The direction can be one of \"up\", \"down\", \"left\", or \"right\".  \\n\\n4. long_press(x: float, y: float)  \\n   This function is used to perform a long press at a specific point on the smartphone screen. The coordinates x and y are normalized values between 0 and 1 representing the position on the screen.  \\n\\n5. call_api(api_name: str, params: str)  \\n   This function is used to call a specified API interface. The parameter `api_name` is a string that describes the name of the API to be called, and `params` is a string containing the relevant input parameters required by the API.  \\n\\n6. navigate_home()  \\n   This function is used to navigate to the home screen of the smartphone.  \\n\\n7. navigate_back()  \\n   This function is used to navigate back to the previous screen on the smartphone.  \\n\\n8. wait(message: Optional[str] = \"\")  \\n   This function is used to wait for a certain period before performing the next action. An optional message can be provided to inform the user why waiting is necessary.  \\n\\n9. enter()  \\n   This function is used to simulate pressing the enter key on the smartphone.  \\n\\n10. finish(message: Optional[str] = \"\")  \\n    This function indicates that the current task is completed and no further actions are needed. An optional string message can be provided for user feedback.  \\n\\n11. take_over(message: Optional[str] = \"\")  \\n    This function is used when the agent determines that the user needs to manually decide or intervene to proceed further. An optional string message can be provided to guide the user.  \\n\\n12. terminate(message: Optional[str] = \"\")  \\n    This function is used to indicate an unexpected termination of the task. An optional string message can be provided to inform the user about the reason for termination.  \\n\\nThe current task instruction is to perform 打开支付宝的余额宝菜单.\\n\\nThese are the key steps to complete the task:\\n1. 我应该在支付宝主页上找到“余额宝”选项并点击它。\\n2. 点击当前页面中部区域的“余额宝”选项。\\n3. 点击当前页面上的“余额宝”选项。\\n4. 不需要任何交互，因为余额宝菜单已经打开。\\n\\n\\nYou can refer to the above key steps, but the action should be flexibly adjusted based on the present screenshot.   \\n\\nNow, given the following screenshot, you need to think and call the function needed to proceed with the task. Your output should be in the given format:  \\nObservation: <Describe what you observe in the image>  \\nThought: <To complete the given task, what is the next step I should do>  \\nAction: <Use the function call with the correct parameters to proceed with the task. If you believe the task is completed or there is nothing to be done, you should call the finish() function. If you believe executing an action will complete the task, add \", finish()\" at the end of the action. You cannot output anything else except a function call in this field.>  \\n\\nYou can only perform one action at a time, so you can only call one function.";

        int stepCounter = 0;
        String response = "";

        while (true) {
            // obtain current screenshot, and upload the img to the nas, replace the last screenshot
            adbcmdReq.setCmd("screencap -p /sdcard/screenshot.png");
            ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);
            adbcmdReq.setCmd("pull /sdcard/screenshot.png ./screenshot");
            ADBUtils.execADBCmdServer(adbcmdReq, jobInstanceDo);

            stepCounter++;
            // start model inference by calling model
            String inferResp = ADBUtils.execInference(imagePath, text, URL_INFERENCE);
            if (Objects.isNull(inferResp)) {
                throw new RuntimeException("model inference failed.");
            }
            if (stepCounter == 1) {
                response = "Observation: The screenshot shows the main page of Alipay. There is a \"余额宝\" option located at the bottom right corner of the screen.\nThought: To open the balance treasure menu, I should tap on the \"余额宝\" option at the bottom right corner of the screen.\nAction: tap(0.9, 0.9)";
            } else if (stepCounter == 2) {
                response = "Observation: The screenshot shows the main page of Alipay. There is a \"余额宝\" option located at the bottom right corner of the screen.\nThought: To open the balance treasure menu, I should tap on the \"余额宝\" option at the bottom right corner of the screen.\nAction: tap(0.9, 0.9), finish()";
            }
            String specAction = ADBUtils.obtainSpecAction(response);

            // Execute parsed actions
            // int[] screenSizeArr = {1080, 2340}; // 示例屏幕尺寸
            int[] screenSizeArr = ADBUtils.parseScreenSize(screenSize);
            boolean execRes = ADBUtils.executeAction(specAction, screenSizeArr, jobInstanceDo);
            log.info("=========================== step: " + stepCounter + " exec finished =======================");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Action execRes: " + execRes);
            if (execRes) {
                // kill all app
                JobEventRegisterCenter.post(new JobStatusEvent(jobInstanceDo.getId(), "SUCCESS"));
                break;
            }
        }
    }
}
