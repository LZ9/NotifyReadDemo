package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.event.NotifyEvent
import com.lodz.android.notifyreaddemo.ui.login.LoginActivity
import com.lodz.android.pandora.base.activity.AbsActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AbsActivity() {


    companion object {
        private const val EXTRA_ACCOUNT_NAME = "extra_account_name"

        fun start(context: Context, account: String) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT_NAME, account)
            context.startActivity(intent)
        }
    }

    private val mAccountTv by bindView<TextView>(R.id.account_tv)
    private val mNotifyAppNameTv by bindView<TextView>(R.id.notify_app_name_tv)
    private val mNotifyTickerTv by bindView<TextView>(R.id.notify_ticker_tv)
    private val mNotifyTitleTv by bindView<TextView>(R.id.notify_title_tv)
    private val mNotifyContentTv by bindView<TextView>(R.id.notify_content_tv)
    private val mLogoutBtn by bindView<MaterialButton>(R.id.logout_btn)
    private lateinit var mAccountName: String

    override fun startCreate() {
        super.startCreate()
        mAccountName = intent?.getStringExtra(EXTRA_ACCOUNT_NAME) ?: ""
    }

    override fun getAbsLayoutId(): Int = R.layout.activity_main

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        mAccountTv.text = StringBuilder().append(getString(R.string.main_account)).append(mAccountName)
        mNotifyAppNameTv.text = StringBuilder().append(getString(R.string.main_notify_app)).append("无")
        mNotifyTickerTv.text = StringBuilder().append(getString(R.string.main_notify_ticker)).append("无")
        mNotifyTitleTv.text = StringBuilder().append(getString(R.string.main_notify_title)).append("无")
        mNotifyContentTv.text = StringBuilder().append(getString(R.string.main_notify_content)).append("无")
    }

    override fun setListeners() {
        super.setListeners()
        mLogoutBtn.setOnClickListener {
            LoginActivity.start(getContext())
            finish()
        }
    }

    override fun onPressBack(): Boolean {
        App.get().exit()
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotifyEvent(event: NotifyEvent) {
        mNotifyAppNameTv.text = StringBuilder().append(getString(R.string.main_notify_app)).append(event.getAppName())
        mNotifyTickerTv.text = StringBuilder().append(getString(R.string.main_notify_ticker)).append(event.ticker)
        mNotifyTitleTv.text = StringBuilder().append(getString(R.string.main_notify_title)).append(event.title)
        mNotifyContentTv.text = StringBuilder().append(getString(R.string.main_notify_content)).append(event.content)
    }
}
