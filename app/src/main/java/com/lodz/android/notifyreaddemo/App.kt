package com.lodz.android.notifyreaddemo

import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.network.NetworkManager
import com.lodz.android.corekt.threadpool.ThreadPoolManager
import com.lodz.android.corekt.utils.UiHandler
import com.lodz.android.pandora.base.application.BaseApplication
import com.tencent.bugly.crashreport.CrashReport

/**
 * @author zhouL
 * @date 2019/8/29
 */
class App :BaseApplication(){

    companion object {
        @JvmStatic
        fun get(): App = BaseApplication.get() as App
    }

    override fun onStartCreate() {
        PrintLog.setPrint(BuildConfig.LOG_DEBUG)// 配置日志开关
        NetworkManager.get().init(this)
        CrashReport.initCrashReport(getApplicationContext(), "b47187b85f", false)
    }

    override fun onExit() {
        UiHandler.destroy()
        ThreadPoolManager.get().releaseAll()
        NetworkManager.get().release(this)
        NetworkManager.get().clearNetworkListener()
    }
}