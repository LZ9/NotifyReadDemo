package com.lodz.android.notifyreaddemo.utils

import com.lodz.android.notifyreaddemo.bean.sms.SmsBean

object BankUtils {

    /** 获取银行卡号 */
    fun getCode(bean: SmsBean): String {
        if (bean.smsType == SmsBean.BANK_CCB_TYPE) {
            return getCodeForCCB(bean)
        }
        if (bean.smsType == SmsBean.BANK_CIB_TYPE) {
            return getCodeForCIB(bean)
        }
        return ""
    }

    /** 获取银行金额 */
    fun getAmount(bean: SmsBean): String {
        if (bean.smsType == SmsBean.BANK_CCB_TYPE) {
            return getAmountForCCB(bean)
        }
        if (bean.smsType == SmsBean.BANK_CIB_TYPE) {
            return getAmountForCIB(bean)
        }
        return ""
    }

    /** 获取兴业银行卡号 */
    fun getCodeForCIB(bean: SmsBean): String {
        try {
            val start = bean.body.indexOf("账户") + 3
            val end = start + 4
            return bean.body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /** 获取兴业银行金额 */
    fun getAmountForCIB(bean: SmsBean): String {
        try {
            val start = bean.body.indexOf("收入") + 2
            var offset = 0
            for (i in start..bean.body.length) {
                if (bean.body[i].toString().equals("元")) {
                    offset = i
                    break
                }
            }
            val end = offset
            return bean.body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /** 获取建设银行卡号 */
    fun getCodeForCCB(bean: SmsBean): String {
        try {
            val start = bean.body.indexOf("尾号") + 2
            val end = start + 4
            return bean.body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /** 获取建设银行金额 */
    fun getAmountForCCB(bean: SmsBean): String {
        try {
            val start = bean.body.indexOf("收入人民币") + 5
            var offset = 0
            for (i in start..bean.body.length) {
                if (bean.body[i].toString().equals("元")) {
                    offset = i
                    break
                }
            }
            val end = offset
            return bean.body.substring(start, end)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}