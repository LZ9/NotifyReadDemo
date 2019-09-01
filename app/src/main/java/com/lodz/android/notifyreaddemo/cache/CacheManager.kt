package com.lodz.android.notifyreaddemo.cache

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.pandora.utils.acache.ACacheUtils

/**
 *
 * @author zhouL
 * @date 2019/9/1
 */
object CacheManager {
    private const val LOCAL_CACHE_KEY = "local_cache_key"

    fun saveSmsList(list: List<SmsBean>) {
        val json = ACacheUtils.get().create().getAsString(LOCAL_CACHE_KEY)
        if (json.isEmpty()) {
            for (i in list.indices) {
                list[i].isSave = true
            }
            ACacheUtils.get().create().put(LOCAL_CACHE_KEY, JSON.toJSONString(list))
            return
        }
        val localList = JSON.parseObject(json, object : TypeReference<List<SmsBean>>() {})
        for (i in localList.indices) {
            for (j in list.indices) {
                if (localList[i].timestamp == list[j].timestamp && localList[i].getVerificationCode().equals(list[j].getVerificationCode())) {
                    list[j].isSave = true
                    break
                }
            }
        }
        val newList = ArrayList<SmsBean>()
        for (bean in list) {
            if (!bean.isSave) {
                newList.add(bean)
            }
        }
        localList.toMutableList().addAll(newList)
        ACacheUtils.get().create().put(LOCAL_CACHE_KEY, JSON.toJSONString(localList))
    }

    fun getNeedUploadList(): List<SmsBean> {
        val list = ArrayList<SmsBean>()
        val json = ACacheUtils.get().create().getAsString(LOCAL_CACHE_KEY)
        if (json.isEmpty()) {
            return list
        }
        val localList = JSON.parseObject(json, object : TypeReference<List<SmsBean>>() {})
        for (localBean in localList) {
            if (!localBean.isUpload) {
                list.add(localBean)
            }
        }
        return list
    }

    fun updateLocalUploadSuccess(bean: SmsBean) {
        val json = ACacheUtils.get().create().getAsString(LOCAL_CACHE_KEY)
        if (json.isEmpty()) {
            return
        }
        val localList = JSON.parseObject(json, object : TypeReference<List<SmsBean>>() {})
        for (i in localList.indices) {
            if (localList[i].timestamp == bean.timestamp && localList[i].getVerificationCode().equals(bean.getVerificationCode())) {
                localList[i].isUpload = true
                break
            }
        }
        ACacheUtils.get().create().put(LOCAL_CACHE_KEY, JSON.toJSONString(localList))
    }

    fun updateLocalUploadSuccess(list: List<SmsBean>) {
        val json = ACacheUtils.get().create().getAsString(LOCAL_CACHE_KEY)
        if (json.isEmpty()) {
            return
        }
        val localList = JSON.parseObject(json, object : TypeReference<List<SmsBean>>() {})
        for (i in localList.indices) {
            for (j in list.indices) {
                if (localList[i].timestamp == list[j].timestamp && localList[i].getVerificationCode().equals(list[j].getVerificationCode())) {
                    localList[i].isUpload = true
                    break
                }
            }
        }
        ACacheUtils.get().create().put(LOCAL_CACHE_KEY, JSON.toJSONString(localList))
    }
}