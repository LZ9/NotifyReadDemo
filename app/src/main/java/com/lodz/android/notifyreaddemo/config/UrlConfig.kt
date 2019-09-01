package com.lodz.android.notifyreaddemo.config

import androidx.annotation.Keep

/**
 * 地址配置
 * @author zhouL
 * @date 2019/3/22
 */
object UrlConfig {

    @Keep
    private const val Release = "https://dz.lfl224552.com/app_api.php/" // 正式地址


    /** 正式环境 */
    var BASE_URL = Release
}