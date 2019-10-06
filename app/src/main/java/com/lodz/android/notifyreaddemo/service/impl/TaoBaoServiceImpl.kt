package com.lodz.android.notifyreaddemo.service.impl

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.Telephony
import com.alibaba.fastjson.JSON
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.utils.DateUtils
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.notifyreaddemo.cache.CacheManager
import com.lodz.android.notifyreaddemo.event.RefreshEvent
import com.lodz.android.notifyreaddemo.event.SmsEvent
import com.lodz.android.notifyreaddemo.service.ServiceContract
import com.lodz.android.pandora.rx.subscribe.observer.BaseObserver
import com.lodz.android.pandora.rx.utils.RxObservableOnSubscribe
import com.lodz.android.pandora.rx.utils.RxUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TaoBaoServiceImpl : ServiceContract {

    private val TAG = "testtag"
    private lateinit var mContext: Context
    private var mDisposable: Disposable? = null

    override fun onCreate(context: Context) {
        mContext = context
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) {
        release()
        mDisposable = Observable.interval(0, 10, TimeUnit.SECONDS)
            .flatMap {
                return@flatMap queryTaoBaoSms()
            }
            .compose(RxUtils.ioToMainObservable())
            .subscribe(Consumer { list ->
                PrintLog.dS(TAG, JSON.toJSONString(list))
                sendCode(list)
            })
    }

    override fun onDestroy() {
        release()
    }

    override fun onRefreshEvent(event: RefreshEvent) {
        queryTaoBaoSms()
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object : BaseObserver<List<SmsBean>>(){
                override fun onBaseNext(any: List<SmsBean>) {
                    PrintLog.dS(TAG, JSON.toJSONString(any))
                    sendCode(any)
                }

                override fun onBaseError(e: Throwable) {
                }
            })
    }

    private fun queryTaoBaoSms(): Observable<List<SmsBean>> =
        Observable.create(object : RxObservableOnSubscribe<List<SmsBean>>() {
            override fun subscribe(emitter: ObservableEmitter<List<SmsBean>>) {
                val list = ArrayList<SmsBean>()
                var cursor: Cursor? = null
                try {
                    cursor = mContext.contentResolver?.query(
                        Telephony.Sms.CONTENT_URI,
                        arrayOf("_id", "address", "body", "date", "type"),
                        "body like ?",
                        arrayOf("%淘宝网%"),
//                        null,null,
                        "date desc limit 50"
                    )
                    if (cursor != null && cursor.count > 0) {
                        while (cursor.moveToNext()) {
                            val bean = SmsBean()
                            bean.id = cursor.getInt(cursor.getColumnIndex("_id"))
                            bean.address = cursor.getString(cursor.getColumnIndex("address"))
                            bean.body = cursor.getString(cursor.getColumnIndex("body"))
                            bean.timestamp = cursor.getLong(cursor.getColumnIndex("date"))
                            bean.date = DateUtils.getFormatString(
                                DateUtils.TYPE_2,
                                Date(bean.timestamp)
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

    private fun sendCode(list: List<SmsBean>) {
        sendNeedUploadList(list)
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object :BaseObserver<List<SmsBean>>(){
                override fun onBaseNext(any: List<SmsBean>) {
                    for (bean in any) {
                        mContext.toastShort(bean.getVerificationCode())
                    }
                    PrintLog.dS("listtag", "need update list : " + JSON.toJSONString(any))
                }
                override fun onBaseError(e: Throwable) {
                }
            })
    }

    private fun sendNeedUploadList(list: List<SmsBean>): Observable<List<SmsBean>> =
        Observable.create(object : RxObservableOnSubscribe<List<SmsBean>>() {
            override fun subscribe(emitter: ObservableEmitter<List<SmsBean>>) {
                try {
                    CacheManager.saveSmsList(list)
                    val results = CacheManager.getNeedUploadList()
                    EventBus.getDefault().post(SmsEvent(results))
                    doNext(emitter, results)
                    doComplete(emitter)
                } catch (e: Exception) {
                    e.printStackTrace()
                    doError(emitter, e)
                }
            }
        })

    private fun release() {
        if (mDisposable != null) {
            mDisposable?.dispose()
            mDisposable = null
        }
    }

}