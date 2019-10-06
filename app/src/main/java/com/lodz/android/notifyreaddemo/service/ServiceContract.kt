package com.lodz.android.notifyreaddemo.service

import android.content.Context
import android.content.Intent
import com.lodz.android.notifyreaddemo.event.RefreshEvent

interface ServiceContract {

    fun onCreate(context: Context)

    fun onStartCommand(intent: Intent?, flags: Int, startId: Int)

    fun onDestroy()

    fun onRefreshEvent(event: RefreshEvent)
}