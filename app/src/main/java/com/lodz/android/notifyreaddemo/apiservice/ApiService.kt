package com.lodz.android.notifyreaddemo.apiservice

import com.lodz.android.notifyreaddemo.bean.response.LoginResponseBean
import com.lodz.android.notifyreaddemo.bean.response.ResponseBean
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("certificate.json")
    fun certificate(): Observable<ResponseBean>

    @FormUrlEncoded
    @POST(" ")
    fun login(@Field("act") act: String, @Field("user") account: String, @Field("pass") pswd: String): Observable<LoginResponseBean>

    /** 接口名[act]，编号[uid]，接口类型[type]，验证码[code]，短信内容[body]，MD5签名[sign] */
    @FormUrlEncoded
    @POST(" ")
    fun sendTaoBaoVerificationCode(
        @Field("act") act: String,
        @Field("uid") uid: String,
        @Field("type") type: String,
        @Field("code") code: String,
        @Field("body") body: String,
        @Field("sign") sign: String
    ): Observable<ResponseBean>

    /** 接口名[act]，编号[uid]，接口类型[type]，卡号[code]，金额[amount]，短信内容[body]，MD5签名[sign] */
    @FormUrlEncoded
    @POST(" ")
    fun sendBankInfo(
        @Field("act") act: String,
        @Field("uid") uid: String,
        @Field("type") type: String,
        @Field("code") code: String,
        @Field("amount") amount: String,
        @Field("body") body: String,
        @Field("sign") sign: String
    ): Observable<ResponseBean>

    @FormUrlEncoded
    @POST(" ")
    fun online(@Field("act") act: String, @Field("uid") uid: String, @Field("sign") sign: String): Observable<ResponseBean>

}