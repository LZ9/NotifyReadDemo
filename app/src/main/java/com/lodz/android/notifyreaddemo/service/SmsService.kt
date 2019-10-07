package com.lodz.android.notifyreaddemo.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.BuildConfig
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.event.RefreshEvent
import com.lodz.android.notifyreaddemo.service.impl.BankServiceImpl
import com.lodz.android.notifyreaddemo.service.impl.TaoBaoServiceImpl
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SmsService : Service() {

    private val SERVICE_ID = 777777

    /** 服务队列  */
    private val mServiceList = ArrayList<ServiceContract>()

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        if (mServiceList.size == 0) {
            if (BuildConfig.APP_TYPE == App.APP_TAOBAO_TYPE){
                mServiceList.add(TaoBaoServiceImpl())
            }
            if (BuildConfig.APP_TYPE == App.APP_BANK_TYPE){
                mServiceList.add(BankServiceImpl())// 建行
            }
        }
        for (contract in mServiceList) {
            contract.onCreate(this)
        }
    }

    private fun getNotification(): Notification {
        val title = BuildConfig.APP_NAME
        val content = "正在为您监测短信信息"
        val builder = NotificationCompat.Builder(applicationContext, App.NOTIFI_GROUP_SERVICE)
        builder.setTicker(title)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setAutoCancel(false)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
        builder.setPriority(NotificationCompat.PRIORITY_MAX)
        return builder.build()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: RefreshEvent) {
        if (mServiceList.size > 0) {
            for (contract in mServiceList) {
                contract.onRefreshEvent(event)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, getNotification())// 启动前台通知
        if (mServiceList.size > 0) {
            for (contract in mServiceList) {
                contract.onStartCommand(intent, flags, startId)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (mServiceList.size > 0) {
            for (contract in mServiceList) {
                contract.onDestroy()
            }
            mServiceList.clear()
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}