package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.corekt.security.MD5
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.apiservice.ApiService
import com.lodz.android.notifyreaddemo.apiservice.ApiServiceManager
import com.lodz.android.notifyreaddemo.bean.response.ResponseBean
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.notifyreaddemo.cache.CacheManager
import com.lodz.android.notifyreaddemo.event.SmsEvent
import com.lodz.android.notifyreaddemo.service.SmsService
import com.lodz.android.notifyreaddemo.ui.login.LoginActivity
import com.lodz.android.pandora.base.activity.AbsActivity
import com.lodz.android.pandora.rx.exception.DataException
import com.lodz.android.pandora.rx.subscribe.observer.BaseObserver
import com.lodz.android.pandora.rx.subscribe.observer.ProgressObserver
import com.lodz.android.pandora.rx.utils.RxUtils
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class MainActivity : AbsActivity() {

    companion object {
        private const val EXTRA_ACCOUNT_NAME = "extra_account_name"
        private const val EXTRA_UID = "extra_uid"
        private const val EXTRA_UPURL = "extra_upurl"
        private const val EXTRA_ACT = "extra_act"
        private const val EXTRA_MSG = "extra_msg"

        fun start(
            context: Context,
            account: String,
            uid: String,
            upurl: String,
            act: String,
            msg: String
        ) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT_NAME, account)
            intent.putExtra(EXTRA_UID, uid)
            intent.putExtra(EXTRA_UPURL, upurl)
            intent.putExtra(EXTRA_ACT, act)
            intent.putExtra(EXTRA_MSG, msg)
            context.startActivity(intent)
        }
    }

    private val TAG = "testtag"

    private val mAccountTv by bindView<TextView>(R.id.account_tv)
    private val mRecyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val mLogoutBtn by bindView<MaterialButton>(R.id.logout_btn)
    private val mClearCacheBtn by bindView<MaterialButton>(R.id.clear_cache_btn)

    private lateinit var mAdapter: SmsAdapter

    private lateinit var mAccountName: String
    private lateinit var mUid: String
    private lateinit var mUpurl: String
    private lateinit var mAct: String
    private lateinit var mMsg: String


    override fun startCreate() {
        super.startCreate()
        mAccountName = intent?.getStringExtra(EXTRA_ACCOUNT_NAME) ?: ""
        mUid = intent?.getStringExtra(EXTRA_UID) ?: ""
        mUpurl = intent?.getStringExtra(EXTRA_UPURL) ?: ""
        mAct = intent?.getStringExtra(EXTRA_ACT) ?: ""
        mMsg = intent?.getStringExtra(EXTRA_MSG) ?: ""
        if (!mUpurl.endsWith(File.separator)) {
            mUpurl += File.separator
        }
    }

    override fun getAbsLayoutId(): Int = R.layout.activity_main

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        mAccountTv.text =
            StringBuilder().append(getString(R.string.main_account)).append(mAccountName)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(getContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        mAdapter = SmsAdapter(getContext())
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = mAdapter
    }

    override fun setListeners() {
        super.setListeners()
        mLogoutBtn.setOnClickListener {
            LoginActivity.start(getContext())
            finish()
        }

        mClearCacheBtn.setOnClickListener {
            CacheManager.clearCache()
            finish()
            App.get().exit()
        }
    }

    override fun initData() {
        super.initData()
        startSmsService()
    }

    override fun onPressBack(): Boolean {
        finish()
        App.get().exit()
        return true
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onNotifyEvent(event: NotifyEvent) {
//        mNotifyAppNameTv.text =
//            StringBuilder().append(getString(R.string.main_notify_app)).append(event.getAppName())
//        mNotifyTickerTv.text =
//            StringBuilder().append(getString(R.string.main_notify_ticker)).append(event.ticker)
//        mNotifyTitleTv.text =
//            StringBuilder().append(getString(R.string.main_notify_title)).append(event.title)
//        mNotifyContentTv.text =
//            StringBuilder().append(getString(R.string.main_notify_content)).append(event.content)
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSmsEvent(event: SmsEvent) {
        showAlreadyUploadSuccess()
        mAdapter.setData(event.list.toMutableList())
        mAdapter.notifyDataSetChanged()

        for (bean in event.list) {
            Observable.zip(
                ApiServiceManager.get().getRetrofit().newBuilder().baseUrl(mUpurl).build().create(
                    ApiService::class.java
                )
                    .sendVerificationCode(
                        mAct,
                        mUid,
                        "tbsms",
                        bean.getVerificationCode(),
                        bean.body,
                        MD5.md(mMsg) ?: ""
                    ),
                Observable.just(bean),
                BiFunction<ResponseBean, SmsBean, SmsBean> { res, bean ->
                    if (res.isSuccess()){
                        CacheManager.updateLocalUploadSuccess(bean)
                        return@BiFunction bean
                    }
                    throw DataException("")
                })
                .compose(RxUtils.ioToMainObservable())
                .subscribe(object : ProgressObserver<SmsBean>() {
                    override fun onPgNext(any: SmsBean) {
                        showAlreadyUploadSuccess()
                    }

                    override fun onPgError(e: Throwable, isNetwork: Boolean) {
                        toastShort(RxUtils.getExceptionTips(e, isNetwork, "验证码上传失败"))
                    }

                }.create(getContext(), "正在上传验证码", false, false))
        }
    }

    private fun showAlreadyUploadSuccess(){
        Observable.just("")
            .map {
                val list = CacheManager.getAlreadyUploadList()
                return@map list
            }
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object :BaseObserver<List<SmsBean>>(){
                override fun onBaseNext(any: List<SmsBean>) {
                    mAdapter.setData(any.toMutableList())
                    mAdapter.notifyDataSetChanged()
                }

                override fun onBaseError(e: Throwable) {
                    mAdapter.setData(ArrayList())
                    mAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun finish() {
        stopSmsService()
        super.finish()
    }

    private fun startSmsService() {
        try {
            val intent = Intent(applicationContext, SmsService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopSmsService() {
        try {
            stopService(Intent(applicationContext, SmsService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


