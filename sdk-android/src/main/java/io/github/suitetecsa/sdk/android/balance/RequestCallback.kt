package io.github.suitetecsa.sdk.android.balance

import android.telephony.TelephonyManager
import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse

/**
 * Interfaz de devolución de llamada para la ejecución de solicitudes de saldo USSD
 */
interface RequestCallback {
    /**
     * Método para obtener la instancia de TelephonyManager asociada con la solicitud de saldo USSD
     * @return a TelephonyManager instance
     */
    val telephonyManager: TelephonyManager?

    /**
     * Método de devolución de llamada invocado cuando se realiza una solicitud de saldo USSD
     * @param request a UssdRequest instance
     */
    fun onRequesting(request: UssdRequest)

    /**
     * Método de devolución de llamada invocado cuando una solicitud de saldo USSD tiene éxito
     *
     * @param request a UssdRequest instance
     * @param response a UssdResponse instance
     */
    fun onSuccess(request: UssdRequest, response: UssdResponse)

    /**
     * Método de devolución de llamada invocado cuando una solicitud de saldo USSD falla
     * @param throwable a Throwable instance
     */
    fun onFailure(throwable: Throwable)
}
