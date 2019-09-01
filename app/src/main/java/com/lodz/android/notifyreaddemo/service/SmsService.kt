package com.lodz.android.notifyreaddemo.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.alibaba.fastjson.JSON
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.utils.DateUtils
import com.lodz.android.notifyreaddemo.App
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.notifyreaddemo.cache.CacheManager
import com.lodz.android.notifyreaddemo.event.SmsEvent
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

class SmsService : Service() {

    private val TAG = "testtag"
    private val SERVICE_ID = 777777

    private var mDisposable: Disposable? = null

    private fun getNotification(): Notification {
        val title = getString(R.string.app_name)
        val content = "正在为您监测短信信息"
        val builder = NotificationCompat.Builder(applicationContext, App.NOTIFI_GROUP_SERVICE)
        builder.setTicker(title)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setAutoCancel(false)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
        builder.setPriority(NotificationCompat.PRIORITY_MAX)
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, getNotification())// 启动前台通知
        release()

        mDisposable = Observable.interval(0, 20, TimeUnit.SECONDS)
            .flatMap {
                return@flatMap querySms()
            }
            .compose(RxUtils.ioToMainObservable())
            .subscribe(Consumer { list ->
                PrintLog.dS(TAG, JSON.toJSONString(list))
                sendCode(list)
            })
        return super.onStartCommand(intent, flags, startId)
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
            .subscribe(BaseObserver.empty())
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


    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() {
        if (mDisposable != null) {
            mDisposable?.dispose()
            mDisposable = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}