package com.fengyongge.rxhttp.bean
/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
data class BaseResponse<T>(val errorMsg: String, val errorCode: String, var data: T)


