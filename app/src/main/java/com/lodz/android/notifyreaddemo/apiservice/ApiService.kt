package com.lodz.android.notifyreaddemo.apiservice

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 *
 * @author zhouL
 * @date 2019/9/1
 */
interface ApiService {

    /** 登录接口 */
    @FormUrlEncoded
    @POST("systemApi/login")
    fun login(@Field("account") account: String, @Field("password") password: String): Observable<String>

    /** post方式获取景点数据 */
    @FormUrlEncoded
    @POST("spot")
    fun postSpot(@Field("id") id: Int): Observable<String>

    /** get方式获取景点数据 */
    @GET("spot")
    fun getSpot(@Query("id") id: Int): Observable<String>

    /** 自定义方式获取景点数据 */
    @POST("spot")
    fun querySpot(@Body requestBody: RequestBody): Observable<String>


    @FormUrlEncoded
    @POST("spot")
    fun sendVerificationCode(@Field("id") code: String): Observable<String>

}