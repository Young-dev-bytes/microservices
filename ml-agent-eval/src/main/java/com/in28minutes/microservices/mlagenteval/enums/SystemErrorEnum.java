package com.in28minutes.microservices.mlagenteval.enums;

import lombok.Getter;
import lombok.Setter;

public enum SystemErrorEnum {
    /* 公共异常 start */
    COMMON_UNKNOWN_ERROR("00001", "系统开小差了，请稍后再试试吧~~"),
    COMMON_REQ_PARAMS_ILLAGE("00003", "请求参数异常"),
    COMMON_INSERT_DATA_ERROR("00004", "存入数据异常"),
    COMMON_OLDER_DATA_ERROR("00005", "投被保人是否一致不可为空"),
    COMMON_INSURED_DATA_ERROR("00006", "多被保人列表不可为空"),
    COMMON_INSURED_MOBILE_ERROR("00007", "被保人手机号不可为空"),
    COMMON_INSURED_URL_ERROR("00008", "被保人双录链接和需要签名的被保人数量不一致"),
    COMMON_INSURED_ID_ERROR("00009", "被保人序号不能为空"),
    // token过期
    COMMON_TOKEN_OVERDUE("00017", "会话过期"),
    COMMON_TOKEN_EMPTY("00018", "会话为空"),
    COMMON_TOKEN_ILLEGAL("00019", "会话非法"),
    COMMON_TOKEN_INVALIDATION("00020", "会话失效，请重新登录"),
    //OkHttp调用异常
    OK_HTTP_ERROR("000005", "OKHttp调用接口异常"),
    /* 公共异常 end */

    /* 登录相关异常 start */
    LOGIN_FAIL("00002", "用户信息不存在,登录失败"),
    MOBILE_FORMAT_ERROR("00006", "手机号格式错误"),
    OLD_PASSWORD_ERROR("00007", "旧密码输入错误"),

    /* 登录相关异常 end */

    /*用户模块异常start*/
    USER_NOT_FOUND("10001", "用户信息不存在"),
    USER_MOBILE_EXIST("10002", "用户手机号已存在"),
    USER_EXIST("10003", "用户已存在"),
    USER_NOT_RECORD("10004", "用户无双录权限"),
    USER_NOT_UPDATE_STATUS("10005", "用户无修改任务单状态权限"),

    /*用户模块异常end*/

    /*智能质检配置模块异常start*/
    QUALITY_CHECK_DELETE_ERROR("10050", "智能质检配置删除失败"),
    QUALITY_CHECK_INSERT_ERROR("10051", "智能质检配置新增失败"),
    QUALITY_CHECK_UNIQUE_ERROR("10052", "该组织下的数据已存在，请勿重复添加!"),
    QUALITY_CHECK_TRANSFER_JSON_ERROR("10053", "请输入正确的json数据格式"),
    QUALITY_CHECK_EDIT_ID_ERROR("10054", "序号不能为空"),
    QUALITY_CHECK_EDIT_ERROR("10055", "修改配置失败"),
    QUALITY_DELETE_ERROR("10056", "不能删除默认的配置项"),
    /*智能质检配置模块异常end*/






    /*腾讯云接口异常*/
    TENCENT_TTS_ERROR("90001", "腾讯云文字转语音异常"),
    ALI_TTS_ERROR("90002", "阿里云文字转语音异常"),

    TENCENT_YUN_API_ERROR("90003", "调用腾讯云相关api error"),
    TENCENT_YUN_NOT_EXIST_ERROR("90004", "从腾讯云没有查询到数据"),
    TENCENT_IM_USERSIG_ERROR("90005", "调用腾讯云获取签名失败"),
    TENCENT_IM_ONLINE_ERROR("90006", "调用腾讯云查询账号在线状态异常"),


    /* 保单相关异常 start*/
    POLICY_POLICYVITALCURRENT_ERROR("00208", "当日机构保单业绩查询无数据，请稍后再试！"),
    POLICY_POLICYVITALBYCONDITION_ERROR("00209", "趋势分析业绩查询无数据，请稍后再试！"),
    POLICY_POLICYLINEVITALBYCONDITION_ERROR("00210", "业绩查询无保单信息，请稍后再试！"),
    POLICY_DETAIL_QUERY_ERROR("00211", "查无此单,请录入正确的保单号！"),
    POLICY_DETAIL_AGENT_ERROR("00212", "非您关联单据,请确认出单机构！"),
    POLICY_ELECTRONICPOLICYADDR_QUERY_ERROR("00213", "电子保单地址获取异常，请稍后再试！"),
    POLICY_COUNT_QUERY_ERROR("00214", "当月保单统计查询异常，请稍后再试！"),


    /*数据导出相关异常 start*/
    EXPORT_ERROR("1038", "导出数据失败"),
    EXPORT_TIME_ERROR("1039", "数据导出时间范围大于30天"),
    SEND_FAIL_TASK_PATH_ERROR("1040", "企微机器人发送消息 未配置文件下载路径或者机器人对于url!"),
    /*数据导出相关异常 end*/

    /* 上传相关异常 */
    UPLOAD_FILE_EMPTY("00601", "上传文件不能为空"),
    UPLOAD_FILE_THAN_MAX("00602", "上传文件最大不能超过20M"),
    UPLOAD_FILE_TYPE_ERROR("00603", "上传文件类型错误"),
    UPLOAD_FILE_ERROR("00604", "上传异常"),
    PDF_COORDINATE_ERROR("00605", "PDF根据关键字获取坐标异常"),
    PDF_PAGENUM_OUTOFLIMIT("00606", "PDF页数超过限制，不能转为图片"),
    PDF_TO_IMAGE_ERROR("00607", "PDF转图片异常"),
    IMAGE_CONVER_ERROR("00608", "图片转换异常"),
    IMAGE_READ_ERROR("00609", "图片读取异常"),
    IMAGE_SAVE_ERROR("00610", "图片保存失败"),
    PDF_LINK_URL_ERROR("00611", "PDF链接地址无效"),
    IMAGE_LINK_URL_ERROR("00612", "图片链接地址无效"),
    FILE_LINK_URL_ERROR("00613", "文件链接地址无效"),
    PDF_SIGN_POSITION_ERROR("00614", "PDF坐标签名异常"),
    PDF_SIGN_KEYWORD_ERROR("00615", "PDF关键字签名异常"),
    PDF_SIGN_ERROR("00616", "PDF签名异常"),


    /*质检相关异常*/
    QC_TASK_INFO_QUERY_ERROR("00701", "质检任务单信息不存在"),

    /*任务单异常*/
    IMPORT_TASK_LIST_ERROR("00800", "导入任务单异常"),
    POLICY_DATA_NOT_EXIST("00801", "话术不存在"),
    TASK_ISSUE_ERROR("00802", "任务单下发异常"),
    IMPORT_TASK_LIST_EMPTY("00803", "保单号或主附险标记为空"),
    NO_MAIN_INSURANCE_POLICY("00804", "有保单没有主险"),
    TASK_INFO_QUERY_ERROR("00805", "任务单信息不存在"),
    AUTOGRAPH_QUERY_ERROR("00806", "投保人签名不存在"),
    INSURANCE_INFO_QUERY_ERROR("00807", "保单信息不存在"),
    PUSH_FILE_QUERY_ERROR("00808", "需推送文件不存在"),
    INSURANCE_FILE_EXIST_ERROR("00809", "相同类型的保单文件已存在"),
    IMPORT_TASK_USERTYPE_ERROR("00810", "双录人员类型错误"),
    POLICYNUMBER_NOT_HAVE_TASK("00811", "该投保单未生成任务"),
    POLICY_NOT_MATCH("00812", "未匹配到话术"),
    IMPORT_TASK_USER_CODE_ERROR("00813", "双录人员类型:销售，双录人员编码不可为空"),
    UPDATE_TASK_STATUS_ERROR("00814", "修改状态类型不为异议件或不成功件"),
    UPDATE_BEFORE_STATUS_ERROR("00815", "修改前状态不为待双录或双录中"),

    POLICY_TEMPLATE_DOWNLOAD_ERROR("00814", "数据导入模板下载失败"),
    IMPORT_TASK_REGION_EMPTY("00815", "出单地区为空"),
    POLICY_HAS_EXIST("00816", "相同类型话术已存在"),
    APPNT_SIGN_TYPE_ERROR("00817", "投保人签名类型错误"),
    APPNT_SIGN_TYPE_EMPTY("00818", "投保人签名类型为空"),
    APPNT_SIGN_PARAM_ERROR("00819", "投保人签名参数格式错误"),
    APPNT_SIGN_PARAM_EMPTY("00820", "投保人签名参数为空"),
    PAGENUM_OVERRUN_IMAGE_ERROR("00821", "页码超过图片数量"),
    PAGENUM_OVERRUN_PDF_ERROR("00822", "页码超过PDF页数"),
    EXIST_DATA_HAS_ISSUE_ERROR("00823", "这批数据中有数据已经被分配，请刷新页面，重新分配"),
    ASSIGN_PERSON_NOT_SERVICE("00824", "分配人员已离职或不是双录服务人员"),
    TRANSFER_PERSON_NOT_SERVICE("00825", "转派人员已离职或不是双录服务人员"),
    TRANSFER_SAME_PERSON("00826", "转派人员和当前双录人员为同一人"),
    DATA_HAS_TRANSFER_ERROR("00827", "该条数据已经被转派，请刷新页面，重新转派"),
    QC_ASSIGN_PERSON_NOT_EXIST("00828", "分配人员已离职或不是双录质检人员"),
    QC_TASK_ISSUE_ERROR("00829", "这批数据中有数据已经被分配，请刷新页面，重新分配"),
    QC_TRANSFER_PERSON_NOT_EXIST("00830", "转派人员已离职或不是双录质检人员"),
    QC_TRANSFER_SAME_PERSON("00831", "转派人员和当前双录质检人员为同一人"),
    QC_TASK_RESULT_ERROR("00832", "质检任务单数据不存在或状态不是待质检"),
    QC_TASK_NOT_EXIST("00833", "质检任务单数据不存在"),
    TASK_NOT_EXIST("00801", "任务单不存在"),
    QC_EMPLOYEE_RESIGN("09001", "重新质检失败，双录人员已离职"),
    QC_TASK_NOT_TO("00834", "质检任务单状态不是待质检"),
    TASK_STATUS_NOT_DRS("00835", "双录任务单状态不是已双录"),
    QC_TASK_STATUS_ERROR("00836", "质检任务单状态异常"),
    TASK_STATUS_ERROR("00837", "双录任务单状态异常"),
    CANNOT_TRANSFER_QCING("00838", "当前双录任务在质检中, 不允许转派"),
    CANNOT_TRANSFER_STATUS("00839", "当前双录任务状态不允许转派"),
    INSURED_SIGN_TYPE_ERROR("00840", "被保人签名类型错误"),
    INSURED_SIGN_PARAM_EMPTY("00841", "被保人签名参数为空"),
    INSURED_SIGN_TYPE_EMPTY("00842", "被保人签名类型为空"),
    INSURED_SIGN_PARAM_ERROR("00843", "被保人签名参数格式错误"),
    APPNT_SIGN_FILE_EMPTY("00844", "投保人签名不能为空"),
    INSURED_SIGN_FILE_EMPTY("00845", "被保人签名不能为空"),
    POLICY_TAB_ERROR("00846", "投被保人一致话术或投被保人不一致话术未配置"),
    INSURED_DRS_URL_ERROR("00846", "被保人双录链接为空"),
    DATA_ERROR("00847", "数据异常"),
    PRODUCT_CODE_ERROR("00848", "产品编码不能为空"),
    PRODUCT_PLAN_CODE_ERROR("00849", "产品计划编码不能为空"),
    POLICY_NUMBER_EMPTY("00850", "投保单号或保单号不能为空"),
    SHORT_URL_CONVERT_ERROR("00851", "双录链接转换异常"),
    CONFIG_IMAGE_IS_EMPTY("00852", "配置图片为空"),
    QC_TASK_PASS_OR_NOT_PASS("00853", "质检任务单状态不是质检通过也不是质检不通过"),
    QC_TASK_ANEW_AUTH_ERROR("00854", "当前用户无权限重新质检"),
    QC_TASK_ANEW_STATUS_ERROR("00855", "当前质检单处于质检不通过且不补录,不可重新质检"),
    QC_TASK_RESULT_REMARK_FILE_EMPTY("00856", "质检备注不能为空"),
    QC_TASK_RESULT_NOT_PASS_NO_REASON("00857", "请选择或输入不通过原因"),
    IMPORT_TASK_PLAN_CODE_EMPTY("00858", "产品计划编码为空"),
    IMPORT_TASK_PLAN_NAME_EMPTY("00859", "产品计划名称为空"),
    IMPORT_TASK_MERCHANT_CODE_EMPTY("00860", "导入任务单商户编码不能为空"),
    IMPORT_TASK_PRODUCT_CODE_EMPTY("00861", "产品编码为空"),
    IMPORT_TASK_PRODUCT_NAME_EMPTY("00862", "产品名称为空"),
    TRANSFER_MERCHANT_ERROR("00863", "转派任务单商户不一致"),


    QC_TASK_NO_NODE_ERROR("00856", "当前质检单处于质检不通过且不补录,不可重新质检"),

    /* 权限系统接口 */
    EPM_ASK_RETURN_NULL("170001", "调用权限系统失败,返回结果为空"),
    EPM_ASK_RETURN_ERROR("170002", "调用权限系统失败,调用接口报错"),
    EPM_GET_USER_INFO_RETURN_NULL("170003", "调用权限系统,用户信息为空"),
    EPM_GET_TENANTS_ERROR("170004", "调用权限系统,获取租户为空或者不唯一"),
    EPM_GET_USER_GRADE_ERROR("170005", "该手机号没有对应用户信息"),

    /*短信错误码*/
    IP_BLACK_LIMIT_ERROR("00900", "当前IP已被限制发送短信，请联系管理员"),
    IP_REQUEST_LIMIT_ERROR("00901", "当前IP今日发送短信次数过多，超过限制次数"),
    MOBILE_REQUEST_LIMIT_ERROR("00902", "当前手机号今日发送短信次数过多，超过限制次数"),
    SEND_SMS_ERROR("00903", "发送短信失败"),
    CHECKCODE_FREQUENCY("00904", "请不要频繁输入验证码，稍后再试。"),
    CHECKCODE_INVALID("00905", "验证码已失效，请重新发送"),
    CHECKCODE_CODE_ERROR("00906", "验证码输入不正确"),

    EPM_GET_TENANT_ID_ERROR("01301", "调用epm接口 获取权限系统中tenantId异常！"),
    EPM_GET_AUTH_TOKEN_ERROR("01302", "调用epm接口 该用户在权限系统中不存在！"),
    EPM_GET_lOGOUT_ERROR("01303", "调用epm接口 用户登出接口失败！"),
    EPM_GET_USER_INFO_ERROR("01304", "调用epm接口 获取权限系统中用户信息接口失败！"),
    EPM_GET_MENU_LIST_ERROR("01305", "调用epm接口 获取权限系统中用户路由信息接口失败！"),
    EPM_GET_TENANT_OVER_ERROR("01305", "调用epm接口 该账户关联过多商户！"),


    CRM_GET_TENANT_INFO_ERROR("11001", "调用CRM接口，获取人员报文信息失败！"),

    /*OCR相关异常*/

    OCR_CARD_UNKNOWN("02001", "上传未知证件类型"),

    /* 规则配置内部异常 */
    DRS_RULE_REPEAT_ERROR("03001", "已存在相同规则"),
    DRS_RULE_ADD_ERROR("03002", "规则添加失败"),
    DRS_RULE_UPDATE_NULL("03003", "修改的规则不存在"),
    DRS_RULE_UPDATE_ERROR("03004", "规则修改失败"),
    DRS_RULE_ID_NOT_NULL("03005", "规则id不能为空"),
    DRS_RULE_REGION_PROVINCE_NOT_NULL("03006", "区域规则省份不能为空"),
    DRS_RULE_DELETE_NULL("03007", "规则删除失败"),
    DRS_RULE_USER_NO_PURVIEW("03008", "该用户无权限操作"),
    DRS_RULE_MERCHANT_NOT_NULL("03009", "规则配置商户不能为空"),

    /*任务池相关异常*/
    DRS_TASK_POOL_REPETITION_ERROR("DRS_03001", "任务单或任务池已存在数据"),
    /*多条主险信息*/
    DRS_MORE_MAIN_INSURANCE_ERROR("DRS_03002", "同一投保单号多条主险信息"),
    /*电子保单地址转换异常*/
    DRS_IMAGE_LINK_URL_ERROR("DRS_03003", "电子保单地址转换异常"),

    DRS_POLICY_NOT_EXIST("DRS_03004", "投保单号不存在"),
    /*规则配置相关异常*/
    DRS_MAIN_INSURANCE_NOT_EXIST("DRS_03005", "主险信息不能为空"),
    DRS_MORE_MAIN_INSURANCE("DRS_03006", "主险信息不能为多条"),
    DRS_FIELD_DEPARTMENT_CODE_IS_NULL("DRS_03007", "外勤部门编码为空"),
    DRS_FIELD_MERCHANT_CODE_IS_NULL("DRS_03008", "商户编码为空"),

    APPLICANT_ID_TYPE_ERROR("18001", "您选择的投保人证件类型与号码不匹配，请重新选择"),

    INSURED_ID_TYPE_ERROR("18002", "您选择的被投保人证件类型与号码不匹配，请重新选择"),

    ;


    @Getter
    private final String errorCode;
    @Setter
    @Getter
    private String errorMsg;

    SystemErrorEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
