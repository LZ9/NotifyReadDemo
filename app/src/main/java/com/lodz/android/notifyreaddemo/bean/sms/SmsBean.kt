package com.lodz.android.notifyreaddemo.bean.sms

class SmsBean {

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

    fun getVerificationCode(): String {
        try {
            val start = body.indexOf("验证码") + 3
            val end = start + 10
            return body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}