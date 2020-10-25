package com.fengyongge.rxhttp.interceptor

import com.fengyongge.rxhttp.core.RxHttp.Companion.getAppContext
import okhttp3.Cache
import java.io.File
/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
object HttpCache {
    private const val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 50 * 1024 * 1024
    fun getCache(): Cache{
        return Cache(
            File(
                getAppContext().cacheDir
                    .absolutePath + File.separator + "data/NetCache"
            ),
            HTTP_RESPONSE_DISK_CACHE_MAX_SIZE.toLong()
        )
    }
}