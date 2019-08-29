package com.lodz.android.notifyreaddemo.ui.login

import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.ui.main.MainActivity
import com.lodz.android.pandora.base.activity.AbsActivity

/**
 * @author zhouL
 * @date 2019/8/29
 */
class LoginActivity : AbsActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val mAccountEdt by bindView<EditText>(R.id.account_edt)
    private val mPswdEdt by bindView<EditText>(R.id.pswd_edt)
    private val mLoginBtn by bindView<MaterialButton>(R.id.login_btn)


    override fun getAbsLayoutId(): Int = R.layout.activity_login

    override fun setListeners() {
        super.setListeners()
        mLoginBtn.setOnClickListener {
            loginAction(mAccountEdt.text.toString(), mPswdEdt.text.toString())
        }
    }

    private fun loginAction(account: String, pswd: String) {
        if (account.isEmpty()) {
            toastShort(R.string.login_account_hint)
            return
        }
        if (pswd.isEmpty()) {
            toastShort(R.string.login_pswd_hint)
            return
        }
        MainActivity.start(getContext(), account)
        finish()
    }

    override fun initData() {
        super.initData()
        if (!isNotifyPermissionGranted()) {
            showNotifyDialog()
        }
    }

    private fun showNotifyDialog() {
        val dialog = AlertDialog.Builder(getContext())
            .setTitle(R.string.notify_dialog_title)
            .setMessage(R.string.notify_dialog_content)
            .setCancelable(false)
            .setPositiveButton(
                R.string.notify_dialog_positive,
                DialogInterface.OnClickListener { dialog, which ->
                    startActivityForResult(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                        Activity.RESULT_FIRST_USER
                    )
                })
            .setNegativeButton(
                R.string.notify_dialog_negative,
                DialogInterface.OnClickListener { dialog, which ->
                    App.get().exit()
                })
            .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_FIRST_USER) {
            if (!isNotifyPermissionGranted()) {
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
                        if (TextUtils.equals(packageName, cn.packageName)) {
                            return true
                        }
                        continue
                    }
                }
            }
        }
        return false
    }
}