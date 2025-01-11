package com.young.microservices.mlagenteval.enums;

import com.young.microservices.mlagenteval.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StopAppCommand {
    STOP_WECHAT("停止微信", "am force-stop com.tencent.mm"),
    STOP_SETTINGS("停止设置", "am force-stop com.android.settings"),
    STOP_DIDI("停止滴滴", "am force-stop com.sdu.didi.psnger"),
    STOP_AMAP("停止高德地图", "am force-stop com.autonavi.minimap"),
    STOP_SF("停止顺丰", "am force-stop com.sf.activity"),
    STOP_MEITUAN("停止美团", "am force-stop com.sankuai.meituan"),
    STOP_QUNAR("停止去哪儿旅行", "am force-stop com.Qunar"),
    STOP_FEIZHU("停止飞猪旅行", "am force-stop com.taobao.trip"),
    STOP_ELONG("停止艺龙旅行", "am force-stop com.dp.android.elong"),
    STOP_CTRIP("停止携程旅行", "am force-stop ctrip.android.view"),
    STOP_TONGCHENG("停止同程旅行", "am force-stop com.tongcheng.android"),
    STOP_TAOBAO("停止淘宝", "am force-stop com.taobao.taobao"),
    STOP_JD("停止京东", "am force-stop com.jingdong.app.mall");

    private final String name;
    private final String value;

    StopAppCommand(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static StopAppCommand fromName(String name) {
        for (StopAppCommand apiCommand : StopAppCommand.values()) {
            if (apiCommand.getName().equals(name)) {
                return apiCommand;
            }
        }
        throw new BusinessException(BizErrorCode.SERVER_ERROR, "No command found for name: " + name);
    }
    public static List<String> getAllStopCmd() {
        return Arrays.stream(StopAppCommand.values())
                .map(StopAppCommand::getValue)
                .collect(Collectors.toList());
    }
}
