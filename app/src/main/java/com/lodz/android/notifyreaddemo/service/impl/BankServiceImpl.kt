package com.lodz.android.notifyreaddemo.service.impl

import android.content.Context
import android.content.Intent
import com.lodz.android.notifyreaddemo.event.RefreshEvent
import com.lodz.android.notifyreaddemo.service.ServiceContract

class BankServiceImpl : ServiceContract {
    override fun onCreate(context: Context) {



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) {


    }

    override fun onDestroy() {



    }

    override fun onRefreshEvent(event: RefreshEvent) {



    }
}