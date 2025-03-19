package io.github.suitetecsa.sdk.android.balance

import android.telephony.TelephonyManager
import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse

interface RequestCallback {
    val telephonyManager: TelephonyManager?
    fun onStateChanged(request: UssdRequest, state: RequestState, retryCount: Int)
    fun onSuccess(request: UssdRequest, response: UssdResponse)
    fun onFailure(throwable: Throwable)
}
