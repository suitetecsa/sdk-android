package io.github.suitetecsa.sdk.android.balance

interface UssdStringCallback {
    fun onSuccess(rawResponse: String)
    fun onFailure(throwable: Throwable)
}
