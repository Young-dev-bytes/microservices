package com.young.microservices.mlagenteval.enums;

public enum ADBCommand {
    SCREENSHOT("screencap -p /sdcard/screenshot.png"),
    PULL_SCREENSHOT("pull /sdcard/screenshot.png"),
    WM_SIZE("wm size"),
    NAVIGATE_BACK("input keyevent KEYCODE_BACK"),
    ENTER("input keyevent 66"),
    NAVIGATE_HOME("input keyevent KEYCODE_HOME");

    private final String value;

    ADBCommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
