package com.lodz.android.notifyreaddemo.config

import androidx.annotation.Keep

object UrlConfig {

    @Keep
    private const val Release = "https://dz.lfl224552.com/app_api.php/" // 正式地址


    /** 正式环境 */
    var BASE_URL = Release
}