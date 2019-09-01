package com.lodz.android.notifyreaddemo.apiservice

import com.lodz.android.notifyreaddemo.bean.response.LoginResponseBean
import com.lodz.android.notifyreaddemo.bean.response.ResponseBean
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 *
 * @author zhouL
 * @date 2019/9/1
 */
interface ApiService {

    @GET("certificate.json")
    fun certificate(): Observable<ResponseBean>

    @FormUrlEncoded
    @POST(" ")
    fun login(@Field("act") act: String, @Field("user") account: String, @Field("pass") pswd: String): Observable<LoginResponseBean>

    @FormUrlEncoded
    @POST(" ")
    fun sendVerificationCode(
        @Field("act") act: String,
        @Field("uid") uid: String,
        @Field("type") type: String,
        @Field("code") code: String,
        @Field("body") body: String,
        @Field("sign") sign: String
    ): Observable<ResponseBean>

    @FormUrlEncoded
    @POST(" ")
    fun online(@Field("act") act: String, @Field("uid") uid: String, @Field("sign") sign: String): Observable<ResponseBean>

}