package io.github.suitetecsa.sdk.android.balance

import android.telephony.TelephonyManager

interface RequestCallback {
    val telephonyManager: TelephonyManager?
    fun onSuccess(response: String)
    fun onFailure(throwable: Throwable)
}
