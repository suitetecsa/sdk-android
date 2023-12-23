package cu.suitetecsa.sdk.android.framework

/**
 * Callback interface for USSD balance requests.
 *
 * This interface defines three callback functions that handle the different stages of a USSD balance request:
 * - `onRequesting`: Called when a USSD request is being sent.
 * - `onSuccess`: Called when a USSD response is received successfully.
 * - `onFailure`: Called when an error occurs during the USSD request.
 */
interface ConsultBalanceCallBack {
    /**
     * Called when a USSD request is being sent.
     *
     * @param consultType The type of USSD consult being requested.
     */
    fun onRequesting(consultType: UssdConsultType)

    /**
     * Called when a USSD response is received successfully.
     *
     * @param ussdResponse The USSD response received.
     */
    fun onSuccess(ussdResponse: UssdResponse)

    /**
     * Called when an error occurs during the USSD request.
     *
     * @param throwable The throwable representing the error.
     */
    fun onFailure(throwable: Throwable)
}
