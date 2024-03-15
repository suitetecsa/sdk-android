package cu.suitetecsa.sdk.android.balance

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.UssdResponse

/**
 * Interfaz de callback para solicitudes de saldo USSD.
 *
 *
 * Esta interfaz define tres funciones de callback que manejan las diferentes etapas de una solicitud de saldo USSD:
 * - `onFetching`: Se llama cuando se está enviando una solicitud USSD para obtener el saldo.
 * - `onSuccess`: Se llama cuando se recibe una respuesta USSD exitosamente.
 * - `onFailure`: Se llama cuando ocurre un error durante la solicitud USSD para obtener el saldo.
 */
interface FetchBalanceCallBack {
    /**
     * Se llama cuando se está enviando una solicitud USSD para obtener el saldo.
     *
     * @param request El tipo de consulta USSD que se está solicitando.
     */
    fun onFetching(request: UssdRequest)

    /**
     * Se llama cuando se recibe una respuesta USSD exitosamente.
     *
     * @param request El tipo de consulta USSD que se está solicitando.
     * @param response La respuesta USSD recibida.
     */
    fun onSuccess(request: UssdRequest, response: UssdResponse)

    /**
     * Se llama cuando ocurre un error durante la solicitud USSD para obtener el saldo.
     *
     * @param throwable El throwable que representa el error.
     */
    fun onFailure(throwable: Throwable)
}
