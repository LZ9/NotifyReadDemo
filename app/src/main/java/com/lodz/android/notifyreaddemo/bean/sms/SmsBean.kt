package com.lodz.android.notifyreaddemo.bean.sms

open class SmsBean {

    companion object {
        /** 淘宝验证码短信 */
        const val TAOBAO_VC_TYPE = 0
        /** 建设银行短信 */
        const val BANK_CCB_TYPE = 1
        /** 兴业银行短信 */
        const val BANK_CIB_TYPE = 2
        /** 福建农信银行短信 */
        const val BANK_FJNX_TYPE = 3
    }

    /** 编号 */
    var id = 0
    /** 发送人 */
    var address = ""
    /** 内容 */
    var body = ""
    /** 日期 */
    var date = ""
    /** 时间戳 */
    var timestamp: Long = 0
    /** 类型 */
    var type = 0
    /** 是否保存本地 */
    var isSave = false
    /** 是否上传后台 */
    var isUpload = false
    /** 短信类型 */
    var smsType = TAOBAO_VC_TYPE
}