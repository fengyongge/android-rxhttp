package com.fengyongge.rxhttp.exception

/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
class ApiException(val errorCode: String, errorMessage: String) : RuntimeException(errorMessage)