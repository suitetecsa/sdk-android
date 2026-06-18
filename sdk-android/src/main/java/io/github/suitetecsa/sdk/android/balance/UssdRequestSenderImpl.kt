package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.balance.exception.UssdRequestException

class UssdRequestSenderImpl : UssdRequestSender {

    private val mainHandler = Handler(Looper.getMainLooper())

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(ussdCode: String, callback: RequestCallback) {
        callback.telephonyManager?.sendUssdRequest(
            ussdCode,
            object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    callback.onSuccess(response.toString())
                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    callback.onFailure(UssdRequestException("Failed with code: $failureCode"))
                }
            },
            mainHandler
        )
    }
}
