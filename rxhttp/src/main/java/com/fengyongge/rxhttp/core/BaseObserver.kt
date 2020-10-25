package com.fengyongge.rxhttp.core

import com.fengyongge.rxhttp.exception.ExceptionHandler
import com.fengyongge.rxhttp.exception.ResponseException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
abstract class BaseObserver<E> : Observer<E> {

    private lateinit var disposable: Disposable

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }

    override fun onNext(data: E) {
        onSuccess(data)
    }

    override fun onError(e: Throwable) {
        var responseException = ExceptionHandler.handle(e)
        onError(responseException)
    }

    override fun onComplete() {

    }

    fun getDisposable() = disposable

    abstract fun onSuccess(data: E)

    abstract fun onError(e: ResponseException)
}