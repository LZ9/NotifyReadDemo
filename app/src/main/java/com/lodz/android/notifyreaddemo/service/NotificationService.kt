package com.lodz.android.notifyreaddemo.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.notifyreaddemo.event.NotifyEvent
import org.greenrobot.eventbus.EventBus

class NotificationService : NotificationListenerService() {

    companion object{
        const val QN_PKG_NAME = "com.taobao.qianniu"

        const val ZFB_PKG_NAME = "com.eg.android.AlipayGphone"

        const val WX_PKG_NAME = "com.tencent.mm"
    }

    private val SERVICE_TAG = "notifyServiceTag"

    private var mData = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mData = intent?.getStringExtra("data") ?: ""
        PrintLog.dS(SERVICE_TAG, "data : $mData")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) {
            return
        }
        if (QN_PKG_NAME.equals(sbn.packageName) || ZFB_PKG_NAME.equals(sbn.packageName) || WX_PKG_NAME.equals(sbn.packageName)){
            sendEvent(sbn, sbn.packageName)
            return
        }
    }

    private fun sendEvent(sbn: StatusBarNotification, pkgName: String) {
        try {
            val ticker = sbn.notification?.tickerText?.toString() ?: ""
            var title = ""
            var content = ""
            PrintLog.iS(SERVICE_TAG, "tickerText : $ticker")
            val extras = sbn.notification.extras
            if (extras != null) {
                title = extras.getString(Notification.EXTRA_TITLE, "")
                content = extras.getString(Notification.EXTRA_TEXT, "")
                PrintLog.iS(SERVICE_TAG, "title : $title")
                PrintLog.iS(SERVICE_TAG, "content : $content")
            }
            EventBus.getDefault().post(NotifyEvent(pkgName, ticker, title, content))
        } catch (e: Exception) {
            e.printStackTrace()
            PrintLog.eS(SERVICE_TAG, "sbn 解析失败")
        }
    }

}