package com.young.microservices.mlagenteval.enums;

import com.young.microservices.mlagenteval.exception.BusinessException;

public enum StartAppCommand {
    START_WECHAT("启动微信", "am force-stop com.tencent.mm && am start -n com.tencent.mm/.ui.LauncherUI"),
    START_SETTINGS("启动设置", "am force-stop com.android.settings && am start -a android.settings.SETTINGS"),
    START_DIDI("启动滴滴", "am force-stop com.sdu.didi.psnger && monkey -p com.sdu.didi.psnger -c android.intent.category.LAUNCHER 1"),
    START_AMAP("启动高德地图", "am force-stop com.autonavi.minimap && monkey -p com.autonavi.minimap -c android.intent.category.LAUNCHER 1"),
    START_SF("启动顺丰", "am force-stop com.sf.activity && monkey -p com.sf.activity -c android.intent.category.LAUNCHER 1"),
    START_MEITUAN("启动美团", "am force-stop com.sankuai.meituan && monkey -p com.sankuai.meituan -c android.intent.category.LAUNCHER 1"),
    START_QUNAR("启动去哪儿旅行", "am force-stop com.Qunar && monkey -p com.Qunar -c android.intent.category.LAUNCHER 1"),
    START_FEIZHU("启动飞猪旅行", "am force-stop com.taobao.trip && monkey -p com.taobao.trip -c android.intent.category.LAUNCHER 1"),
    START_ELONG("启动艺龙旅行", "am force-stop com.dp.android.elong && monkey -p com.dp.android.elong -c android.intent.category.LAUNCHER 1"),
    START_CTRIP("启动携程旅行", "am force-stop ctrip.android.view && monkey -p ctrip.android.view -c android.intent.category.LAUNCHER 1"),
    START_TONGCHENG("启动同程旅行", "am force-stop com.tongcheng.android && monkey -p com.tongcheng.android -c android.intent.category.LAUNCHER 1"),
    START_TAOBAO("启动淘宝", "am force-stop com.taobao.taobao && monkey -p com.taobao.taobao -c android.intent.category.LAUNCHER 1"),
    START_JD("启动京东", "am force-stop com.jingdong.app.mall && monkey -p com.jingdong.app.mall -c android.intent.category.LAUNCHER 1");

    private final String name;
    private final String value;

    StartAppCommand(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static StartAppCommand fromName(String name) {
        for (StartAppCommand apiCommand : StartAppCommand.values()) {
            if (apiCommand.getName().equals(name)) {
                return apiCommand;
            }
        }
        throw new BusinessException(BizErrorCode.SERVER_ERROR, "No command found for name: " + name);
    }
}
