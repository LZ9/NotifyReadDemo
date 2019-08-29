package com.lodz.android.notifyreaddemo.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.notifyreaddemo.event.NotifyEvent
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author zhouL
 * @date 2019/8/29
 */
class NotificationService : NotificationListenerService() {

    private val SERVICE_TAG = "notifyServiceTag"

    private val QN_PKG_NAME = "com.taobao.qianniu"

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
        if (!QN_PKG_NAME.equals(sbn.packageName)){
            return
        }
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
            EventBus.getDefault().post(NotifyEvent(ticker, title, content))
        } catch (e: Exception) {
            e.printStackTrace()
            PrintLog.eS(SERVICE_TAG, "sbn 解析失败")
        }
    }

}