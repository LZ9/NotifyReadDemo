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
import com.lodz.android.notifyreaddemo.event.BankSmsEvent
import com.lodz.android.notifyreaddemo.event.RefreshEvent
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

class BankServiceImpl : ServiceContract {

    private val TAG = "banksms"
    private lateinit var mContext: Context
    private var mDisposable: Disposable? = null

    override fun onCreate(context: Context) {
        mContext = context
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) {
        release()
        mDisposable = Observable.interval(0, 10, TimeUnit.SECONDS)
            .flatMap {
                return@flatMap Observable.merge(
                    queryBankSms("建设银行", SmsBean.BANK_CCB_TYPE),
                    queryBankSms("兴业银行", SmsBean.BANK_CIB_TYPE)
                )
            }
            .flatMap { list ->
                return@flatMap sendNeedUploadList(list)
            }
            .compose(RxUtils.ioToMainObservable())
            .subscribe(Consumer { list ->
                PrintLog.dS(TAG, JSON.toJSONString(list))
                EventBus.getDefault().post(BankSmsEvent(list))
            })
    }

    override fun onDestroy() {
        release()
    }

    override fun onRefreshEvent(event: RefreshEvent) {
        Observable.merge(
            queryBankSms("建设银行", SmsBean.BANK_CCB_TYPE),
            queryBankSms("兴业银行", SmsBean.BANK_CIB_TYPE)
        )
            .flatMap { list ->
                return@flatMap sendNeedUploadList(list)
            }
            .compose(RxUtils.ioToMainObservable())
            .subscribe(object : BaseObserver<List<SmsBean>>() {
                override fun onBaseNext(any: List<SmsBean>) {
                    EventBus.getDefault().post(BankSmsEvent(any))
                    PrintLog.dS(TAG, JSON.toJSONString(any))
                    for (bean in any) {
                        mContext.toastShort(bean.body)
                    }
                }

                override fun onBaseError(e: Throwable) {
                }
            })
    }

    private fun queryBankSms(bankName: String, bankType: Int): Observable<List<SmsBean>> =
        Observable.create(object : RxObservableOnSubscribe<List<SmsBean>>() {
            override fun subscribe(emitter: ObservableEmitter<List<SmsBean>>) {
                val list = ArrayList<SmsBean>()
                var cursor: Cursor? = null
                try {
                    cursor = mContext.contentResolver?.query(
                        Telephony.Sms.CONTENT_URI,
                        arrayOf("_id", "address", "body", "date", "type"),
                        "body like ?",
                        arrayOf("%" + bankName + "%"),
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
                            bean.smsType = bankType
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

    private fun sendNeedUploadList(list: List<SmsBean>): Observable<List<SmsBean>> =
        Observable.create(object : RxObservableOnSubscribe<List<SmsBean>>() {
            override fun subscribe(emitter: ObservableEmitter<List<SmsBean>>) {
                try {
                    CacheManager.saveSmsList(list)
                    val results = CacheManager.getNeedUploadList()
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