package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.google.android.material.button.MaterialButton
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.utils.DateUtils
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.bean.SmsBean
import com.lodz.android.notifyreaddemo.event.NotifyEvent
import com.lodz.android.notifyreaddemo.ui.login.LoginActivity
import com.lodz.android.pandora.base.activity.AbsActivity
import com.lodz.android.pandora.rx.subscribe.observer.BaseObserver
import com.lodz.android.pandora.rx.utils.RxObservableOnSubscribe
import com.lodz.android.pandora.rx.utils.RxUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AbsActivity() {

    companion object {
        private const val EXTRA_ACCOUNT_NAME = "extra_account_name"
        private const val TAG = "testtag"


        fun start(context: Context, account: String) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT_NAME, account)
            context.startActivity(intent)
        }
    }

    private val TAG = "testtag"

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

    override fun initData() {
        super.initData()
        querySms()
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object :BaseObserver<List<SmsBean>>(){
                override fun onBaseNext(any: List<SmsBean>) {
                    PrintLog.iS(TAG, JSON.toJSONString(any))
                }
                override fun onBaseError(e: Throwable) {}
            })
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

    private fun querySms(): Observable<List<SmsBean>> =
        Observable.create(object : RxObservableOnSubscribe<List<SmsBean>>() {
            override fun subscribe(emitter: ObservableEmitter<List<SmsBean>>) {
                val uri = Uri.parse("content://sms/")
                val list = ArrayList<SmsBean>()
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver?.query(
                        uri,
                        arrayOf("_id", "address", "body", "date", "type"),
                        null,
                        null,
                        "date desc limit 3 offset 0"
                    )
                    if (cursor != null && cursor.count > 0) {
                        cursor.moveToFirst()
                        while (cursor.moveToNext()) {
                            val bean = SmsBean()
                            bean.id = cursor.getInt(cursor.getColumnIndex("_id"))
                            bean.address = cursor.getString(cursor.getColumnIndex("address"))
                            bean.body = cursor.getString(cursor.getColumnIndex("body"))
                            bean.date = DateUtils.getFormatString(
                                DateUtils.TYPE_2,
                                Date(cursor.getLong(cursor.getColumnIndex("date")))
                            )
                            bean.type = cursor.getInt(cursor.getColumnIndex("type"))
                            list.add(bean)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
                doNext(emitter, list)
                doComplete(emitter)
            }
        })
}
