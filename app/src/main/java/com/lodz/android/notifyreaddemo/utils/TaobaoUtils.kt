package com.lodz.android.notifyreaddemo.utils

import com.lodz.android.notifyreaddemo.bean.sms.SmsBean

object TaobaoUtils {

    /** 获取淘宝验证码 */
    fun getVerificationCode(bean: SmsBean): String {
        try {
            val start = bean.body.indexOf("验证码") + 3
            val end = start + 10
            return bean.body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}