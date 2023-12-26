package cu.suitetecsa.sdk.android.framework

import android.telephony.TelephonyManager

/**
 * Callback interface for USSD balance request execution.
 */
interface UssdBalanceRequestExecutorCallBack {
    /**
     * The TelephonyManager instance associated with the USSD balance request.
     */
    val telephonyManager: TelephonyManager

    /**
     * Callback function invoked when a USSD balance request is being made.
     *
     * @param consultType The type of USSD consult being requested.
     */
    fun onRequesting(consultType: UssdConsultType)

    /**
     * Callback function invoked when a USSD balance request is successful.
     *
     * @param ussdResponse The USSD response received.
     */
    fun onSuccess(ussdResponse: UssdResponse)

    /**
     * Callback function invoked when a USSD balance request fails.
     *
     * @param throwable The throwable representing the failure reason.
     */
    fun onFailure(throwable: Throwable)
}
