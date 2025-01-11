package com.young.microservices.mlagenteval.enums;

import com.young.microservices.mlagenteval.exception.BusinessException;

public enum AppPackNameEnum {
    WECHAT("微信", "com.tencent.mm"),
    SETTINGS("设置", "com.android.settings"),
    DIDI("滴滴", "com.sdu.didi.psnger"),
    AMAP("高德地图", "com.autonavi.minimap"),
    SF("顺丰", "com.sf.activity"),
    MEITUAN("美团", "com.sankuai.meituan"),
    QUNAR("去哪儿旅行", "com.Qunar"),
    FEIZHU("飞猪旅行", "com.taobao.trip"),
    ELONG("艺龙旅行", "com.dp.android.elong"),
    CTRIP("携程旅行", "ctrip.android.view"),
    TONGCHENG("同程旅行", "com.tongcheng.android"),
    TAOBAO("淘宝", "com.taobao.taobao"),
    JD("京东", "com.jingdong.app.mall");

    private final String name;
    private final String value;

    AppPackNameEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static AppPackNameEnum fromName(String name) {
        for (AppPackNameEnum apiCommand : AppPackNameEnum.values()) {
            if (apiCommand.getName().equals(name)) {
                return apiCommand;
            }
        }
        throw new BusinessException(BizErrorCode.SERVER_ERROR, "No command found for name: " + name);
    }
}
