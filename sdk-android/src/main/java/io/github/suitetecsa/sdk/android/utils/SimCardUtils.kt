package io.github.suitetecsa.sdk.android.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import io.github.suitetecsa.sdk.android.balance.RequestCallback
import io.github.suitetecsa.sdk.android.balance.UssdRequestSender
import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse
import io.github.suitetecsa.sdk.android.model.SimCard

object SimCardUtils {
    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun makeCall(simCard: SimCard, context: Context, phoneNumber: String) {
        val intent =
            Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phoneNumber.replace("#", "%23")))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(
                "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                simCard.subscriptionId.toString()
            )
        } else {
            intent.putExtra(
                "con.android.phone.extra.PHONE_ACCOUNT_HANDLE",
                simCard.subscriptionId.toString()
            )
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun smartFetchBalance(simCard: SimCard, callBack: FetchBalanceCallBack) {
        val requestCallback: RequestCallback = object : RequestCallback {
            override val telephonyManager: TelephonyManager
                get() = simCard.telephony

            override fun onRequesting(request: UssdRequest) {
                callBack.onFetching(request)
            }

            override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                callBack.onSuccess(request, response)
            }

            override fun onFailure(throwable: Throwable) {
                callBack.onFailure(throwable)
            }
        }
        UssdRequestSender.Builder().build().send(requestCallback)
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun ussdFetch(simCard: SimCard, ussdCode: String, callBack: FetchBalanceCallBack) {
        val requestCallback: RequestCallback = object : RequestCallback {
            override val telephonyManager: TelephonyManager
                get() = simCard.telephony

            override fun onRequesting(request: UssdRequest) {
                callBack.onFetching(request)
            }

            override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                callBack.onSuccess(request, response)
            }

            override fun onFailure(throwable: Throwable) {
                callBack.onFailure(throwable)
            }
        }
        UssdRequestSender.Builder().build().send(ussdCode, requestCallback)
    }
}

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.makeCall(context: Context, phoneNumber: String) =
    SimCardUtils.makeCall(this, context, phoneNumber)

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.smartFetchBalance(callBack: FetchBalanceCallBack) =
    SimCardUtils.smartFetchBalance(this, callBack)

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.ussdFetch(ussdCode: String, callBack: FetchBalanceCallBack) =
    SimCardUtils.ussdFetch(this, ussdCode, callBack)
