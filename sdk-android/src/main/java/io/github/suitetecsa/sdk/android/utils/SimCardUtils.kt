package io.github.suitetecsa.sdk.android.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import io.github.suitetecsa.sdk.android.balance.RequestCallback
import io.github.suitetecsa.sdk.android.balance.UssdRequestSender
import io.github.suitetecsa.sdk.android.balance.UssdStringCallback
import io.github.suitetecsa.sdk.android.model.SimCard

object SimCardUtils {
    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun ussdFetch(simCard: SimCard, ussdCode: String, callBack: UssdStringCallback) {
        val requestCallback: RequestCallback = object : RequestCallback {
            override val telephonyManager: TelephonyManager
                get() = simCard.telephony

            override fun onSuccess(response: String) {
                callBack.onSuccess(response)
            }

            override fun onFailure(throwable: Throwable) = callBack.onFailure(throwable)
        }
        UssdRequestSender.Builder().build().send(ussdCode, requestCallback)
    }
}

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.ussdFetch(ussdCode: String, callBack: UssdStringCallback) =
    SimCardUtils.ussdFetch(this, ussdCode, callBack)
