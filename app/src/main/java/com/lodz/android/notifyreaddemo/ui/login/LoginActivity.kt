package com.lodz.android.notifyreaddemo.ui.login

import android.content.Context
import android.content.Intent
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.apiservice.ApiService
import com.lodz.android.notifyreaddemo.apiservice.ApiServiceManager
import com.lodz.android.notifyreaddemo.bean.response.LoginResponseBean
import com.lodz.android.notifyreaddemo.ui.main.MainActivity
import com.lodz.android.pandora.base.activity.AbsActivity
import com.lodz.android.pandora.rx.subscribe.observer.ProgressObserver
import com.lodz.android.pandora.rx.utils.RxUtils
import java.io.File

class LoginActivity : AbsActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val mUrlEdt by bindView<EditText>(R.id.http_edt)
    private val mAccountEdt by bindView<EditText>(R.id.account_edt)
    private val mPswdEdt by bindView<EditText>(R.id.pswd_edt)
    private val mLoginBtn by bindView<MaterialButton>(R.id.login_btn)


    override fun getAbsLayoutId(): Int = R.layout.activity_login

    override fun setListeners() {
        super.setListeners()
        mLoginBtn.setOnClickListener {
            loginAction(
                mUrlEdt.text.toString(),
                mAccountEdt.text.toString(),
                mPswdEdt.text.toString()
            )
        }
    }

    private fun loginAction(url: String, account: String, pswd: String) {
        var urls = url
        if (urls.isEmpty()) {
            toastShort(R.string.login_http_hint)
            return
        }
        if (!urls.endsWith(File.separator)) {
            urls += File.separator
        }
        if (account.isEmpty()) {
            toastShort(R.string.login_account_hint)
            return
        }
        if (pswd.isEmpty()) {
            toastShort(R.string.login_pswd_hint)
            return
        }

//        ApiServiceManager.get()
//            .getRetrofit().newBuilder().baseUrl("https://raw.githubusercontent.com/LZ9/NotifyReadDemo/master/").build()
//            .create(ApiService::class.java)
//            .certificate()
//            .compose(RxUtils.ioToMainObservable())
//            .subscribe(object : ProgressObserver<ResponseBean>() {
//                override fun onPgNext(any: ResponseBean) {
//                    login(url, account, pswd)
//                }
//
//                override fun onPgError(e: Throwable, isNetwork: Boolean) {
//                    toastShort(RxUtils.getExceptionTips(e, isNetwork, "校验失败"))
//                }
//            }.create(getContext(), "正在校验接口", false, false))

        login(urls, account, pswd)
    }

    private fun login(url: String, account: String, pswd: String) {
        ApiServiceManager.get()
            .getRetrofit().newBuilder().baseUrl(url).build()
            .create(ApiService::class.java).login("login", account, pswd)
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object : ProgressObserver<LoginResponseBean>() {
                override fun onPgNext(any: LoginResponseBean) {
                    MainActivity.start(getContext(), account, any.uid, any.upurl, any.act, any.msg)
                    finish()
                }

                override fun onPgError(e: Throwable, isNetwork: Boolean) {
                    toastShort(RxUtils.getExceptionTips(e, isNetwork, "登录失败"))
                }
            }.create(getContext(), "正在登录", false, false))
    }

    override fun initData() {
        super.initData()
        mUrlEdt.setText("http://dai.lfl224552.com/app_api.php")
        mUrlEdt.setSelection(0)
        mAccountEdt.setText("475750234")
        mPswdEdt.setText("111111")

//        if (!isNotifyPermissionGranted()) {
//            showNotifyDialog()
//        }
    }

    override fun onPressBack(): Boolean {
        finish()
        return true
    }

    override fun finish() {
        super.finish()
        App.get().exit()
    }

//    private fun showNotifyDialog() {
//        AlertDialog.Builder(getContext())
//            .setTitle(R.string.notify_dialog_title)
//            .setMessage(R.string.notify_dialog_content)
//            .setCancelable(false)
//            .setPositiveButton(
//                R.string.notify_dialog_positive,
//                DialogInterface.OnClickListener { dialog, which ->
//                    startActivityForResult(
//                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
//                        Activity.RESULT_FIRST_USER
//                    )
//                })
//            .setNegativeButton(
//                R.string.notify_dialog_negative,
//                DialogInterface.OnClickListener { dialog, which ->
//                    App.get().exit()
//                })
//            .create()
//            .show()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Activity.RESULT_FIRST_USER) {
//            if (!isNotifyPermissionGranted()) {
//                showNotifyDialog()
//            }
//        }
//    }
//
//    private fun isNotifyPermissionGranted(): Boolean {
//        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
//        if (!flat.isNullOrEmpty()) {
//            val names = flat.split(":")
//            for (name in names) {
//                val cn = ComponentName.unflattenFromString(name)
//                if (cn != null) {
//                    if (TextUtils.equals(packageName, cn.packageName)) {
//                        return true
//                    }
//                    continue
//                }
//            }
//        }
//        return false
//    }
}