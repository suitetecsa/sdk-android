package cu.suitetecsa.sdk.android.framework

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.Utils
import cu.suitetecsa.sdk.android.domain.model.SimCard

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
fun SimCard.consultBalance(callBack: ConsultBalanceCallBack) {
    val ussdBalanceRequestExecutorCallBack = object : UssdBalanceRequestExecutorCallBack {
        override val telephonyManager: TelephonyManager
            get() = this@consultBalance.telephony!!

        override fun onRequesting(consultType: UssdConsultType) = callBack.onRequesting(consultType)

        override fun onSuccess(ussdResponse: UssdResponse) = callBack.onSuccess(ussdResponse)

        override fun onFailure(throwable: Throwable) = callBack.onFailure(throwable)
    }
    UssdBalanceCommandExecutor.execute(ussdBalanceRequestExecutorCallBack)
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(android.Manifest.permission.CALL_PHONE)
fun SimCard.ussdExecute(ussdCode: String, callBack: ConsultBalanceCallBack) {
    val ussdBalanceRequestExecutorCallBack = object : UssdBalanceRequestExecutorCallBack {
        override val telephonyManager: TelephonyManager
            get() = this@ussdExecute.telephony!!

        override fun onRequesting(consultType: UssdConsultType) = callBack.onRequesting(consultType)

        override fun onSuccess(ussdResponse: UssdResponse) = callBack.onSuccess(ussdResponse)

        override fun onFailure(throwable: Throwable) = callBack.onFailure(throwable)
    }
    UssdBalanceCommandExecutor.execute(ussdCode, ussdBalanceRequestExecutorCallBack)
}

@RequiresPermission(android.Manifest.permission.CALL_PHONE)
fun SimCard.makeCall(context: Context, phoneNumber: String) {
    Utils.makeCall(context, phoneNumber, this)
}
