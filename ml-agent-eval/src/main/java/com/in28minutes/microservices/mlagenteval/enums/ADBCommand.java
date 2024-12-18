package com.in28minutes.microservices.mlagenteval.enums;

public enum ADBCommand {
    WM_SIZE("wm size"),
    SCREENSHOT("screen"), PULL_SCREENSHOT(" ");

    private String value;

    ADBCommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
