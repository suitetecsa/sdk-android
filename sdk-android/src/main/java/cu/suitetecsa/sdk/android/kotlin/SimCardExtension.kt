package cu.suitetecsa.sdk.android.kotlin

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import cu.suitetecsa.sdk.android.balance.RequestCallback
import cu.suitetecsa.sdk.android.balance.UssdRequestSender
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.model.SimCard
import cu.suitetecsa.sdk.android.utils.SimCardUtils

/**
 * Consults the balance using USSD requests.
 *
 * This function sends USSD requests to consult the balance and handles the responses through the provided callback.
 * It requires the CALL_PHONE permission to function correctly.
 *
 * @param callBack The callback interface to handle USSD responses.
 * @throws SecurityException if the CALL_PHONE permission is not granted.
 */
@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(android.Manifest.permission.CALL_PHONE)
fun SimCard.smartFetchBalance(callBack: FetchBalanceCallBack) {
    val requestCallback = object : RequestCallback {

        override fun getTelephonyManager(): TelephonyManager = this@smartFetchBalance.telephony()!!

        override fun onRequesting(request: UssdRequest) = callBack.onFetching(request)

        override fun onSuccess(
            request: UssdRequest,
            response: UssdResponse
        ) = callBack.onSuccess(request, response)

        override fun onFailure(throwable: Throwable) = callBack.onFailure(throwable)
    }
    UssdRequestSender.Builder().build().send(requestCallback)
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(android.Manifest.permission.CALL_PHONE)
fun SimCard.ussdFetch(ussdCode: String, callBack: FetchBalanceCallBack) {
    val requestCallback = object : RequestCallback {
        override fun getTelephonyManager(): TelephonyManager = this@ussdFetch.telephony()!!

        override fun onRequesting(request: UssdRequest) = callBack.onFetching(request)

        override fun onSuccess(
            request: UssdRequest,
            response: UssdResponse
        ) = callBack.onSuccess(request, response)

        override fun onFailure(throwable: Throwable) = callBack.onFailure(throwable)
    }
    UssdRequestSender.Builder().build().send(ussdCode, requestCallback)
}

@RequiresPermission(android.Manifest.permission.CALL_PHONE)
fun SimCard.makeCall(context: Context, phoneNumber: String) {
    SimCardUtils.makeCall(this, context, phoneNumber)
}
