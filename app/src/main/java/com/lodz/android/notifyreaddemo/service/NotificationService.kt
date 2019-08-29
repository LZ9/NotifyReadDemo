package com.lodz.android.notifyreaddemo.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lodz.android.corekt.log.PrintLog

/**
 *
 * @author zhouL
 * @date 2019/8/29
 */
class NotificationService : NotificationListenerService() {

    private val SERVICE_TAG = "notifyServiceTag"

    private var mData = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mData = intent?.getStringExtra("data") ?: ""
        PrintLog.dS(SERVICE_TAG, "data : $mData")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null){
            return
        }
        try {
            val tickerText = sbn.notification?.tickerText?.toString() ?: ""
            val extras = sbn.notification.extras
            if (extras != null) {
                val title = extras.getString(Notification.EXTRA_TITLE, "")
                val content = extras.getString(Notification.EXTRA_TEXT, "")
                PrintLog.iS(SERVICE_TAG, "title : $title")
                PrintLog.iS(SERVICE_TAG, "content : $content")
            }
            PrintLog.iS(SERVICE_TAG, "tickerText : $tickerText")
        }catch (e:Exception){
            e.printStackTrace()
            PrintLog.eS(SERVICE_TAG, "sbn 解析失败")
        }
    }

}