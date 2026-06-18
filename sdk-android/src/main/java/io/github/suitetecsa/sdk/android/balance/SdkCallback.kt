package io.github.suitetecsa.sdk.android.balance

interface SdkCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(throwable: Throwable)
}
