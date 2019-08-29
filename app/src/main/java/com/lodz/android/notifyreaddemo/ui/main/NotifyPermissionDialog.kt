package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.pandora.widget.dialog.BaseDialog

/**
 * @author zhouL
 * @date 2019/8/29
 */
class NotifyPermissionDialog(context: Context) :BaseDialog(context){

    override fun getLayoutId(): Int = R.layout.dialog_notify_permission;


}