package com.lodz.android.notifyreaddemo.event

import com.lodz.android.notifyreaddemo.service.NotificationService

class NotifyEvent(val pkgName: String, val ticker: String, val title: String, val content: String) {

    fun getAppName(): String = when (pkgName) {
        NotificationService.QN_PKG_NAME -> "千牛"
        NotificationService.ZFB_PKG_NAME -> "支付宝"
        NotificationService.WX_PKG_NAME -> "微信"
        else -> ""
    }
}