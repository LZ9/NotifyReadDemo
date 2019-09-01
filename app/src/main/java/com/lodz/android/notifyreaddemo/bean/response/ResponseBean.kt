package com.lodz.android.notifyreaddemo.bean.response

import com.lodz.android.pandora.rx.status.ResponseStatus

open class ResponseBean :ResponseStatus{


    companion object{
        const val SUCCESS = 1
        const val FAIL = 0
    }

    var code = SUCCESS

    var msg = ""

    override fun isSuccess(): Boolean = code == SUCCESS

    override fun valueMsg(): String = msg

    override fun valueStatus(): String = code.toString()
}