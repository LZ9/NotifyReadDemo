package com.lodz.android.notifyreaddemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.network.NetworkManager
import com.lodz.android.corekt.threadpool.ThreadPoolManager
import com.lodz.android.corekt.utils.NotificationUtils
import com.lodz.android.corekt.utils.UiHandler
import com.lodz.android.pandora.base.application.BaseApplication
import com.lodz.android.pandora.utils.acache.ACacheUtils
import com.tencent.bugly.crashreport.CrashReport

class App : BaseApplication() {

    companion object {
        const val NOTIFI_GROUP_SERVICE = "group_service"
        const val APP_TAOBAO_TYPE = 1
        const val APP_BANK_TYPE = 2

        fun get(): App = BaseApplication.get() as App
    }

    override fun onStartCreate() {
        PrintLog.setPrint(BuildConfig.LOG_DEBUG)// 配置日志开关
        NetworkManager.get().init(this)
        CrashReport.initCrashReport(getApplicationContext(), "b47187b85f", false)
        initNotifyChannel(this)// 初始化通知通道
        initACache(this)
    }

    /** 初始化ACache缓存 */
    private fun initACache(context: Context) {
        ACacheUtils.get().newBuilder()
            .setCacheDir(context.cacheDir.absolutePath)// 设置缓存路径，不设置则使用默认路径
            .build(context)// 完成构建
    }

    /** 初始化通知通道 */
    private fun initNotifyChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationUtils.create(context).createNotificationChannelGroup(
                NotificationChannelGroup(NOTIFI_GROUP_SERVICE, "后台服务通知组")
            )
            val channel = getServiceChannel()
            if (channel != null){
                NotificationUtils.create(context).createNotificationChannel(channel)
            }
        }
    }

    private fun getServiceChannel(): NotificationChannel? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFI_GROUP_SERVICE,
                "服务通知",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.description = "应用服务通知频道"
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 400, 200, 100)
            channel.canBypassDnd()
            channel.setBypassDnd(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.setShowBadge(false)
            channel.group = NOTIFI_GROUP_SERVICE
            return channel
        }
        return null
    }

    override fun onExit() {
        UiHandler.destroy()
        ThreadPoolManager.get().releaseAll()
        NetworkManager.get().release(this)
        NetworkManager.get().clearNetworkListener()
    }
}