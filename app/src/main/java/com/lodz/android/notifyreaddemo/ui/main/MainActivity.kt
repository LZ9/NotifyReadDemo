package com.lodz.android.notifyreaddemo.ui.main

import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.pandora.base.activity.BaseActivity


class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initData() {
        super.initData()
        if (!isNotifyPermissionGranted()){
            showNotifyDialog()
        }
    }

    private fun showNotifyDialog() {
        val dialog = AlertDialog.Builder(getContext())
            .setTitle("通知读取权限")
            .setMessage("请开启通知读取权限")
            .setCancelable(false)
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                startActivityForResult(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                    Activity.RESULT_FIRST_USER
                )
            })
            .setNegativeButton("退出", DialogInterface.OnClickListener { dialog, which ->
                App.get().exit()
            })
            .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_FIRST_USER) {
            if (!isNotifyPermissionGranted()){
                showNotifyDialog()
            }
        }
    }

    private fun isNotifyPermissionGranted(): Boolean {
        val manager: NotificationManager =
            getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return manager.isNotificationListenerAccessGranted(
                ComponentName.unflattenFromString(
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                )
            )
        } else {
            val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            if (!flat.isNullOrEmpty()) {
                val names = flat.split(":")
                for (name in names) {
                    val cn = ComponentName.unflattenFromString(name)
                    if (cn != null) {
                        return TextUtils.equals(packageName, cn.packageName)
                    }
                }
            }
        }
        return false
    }
}
