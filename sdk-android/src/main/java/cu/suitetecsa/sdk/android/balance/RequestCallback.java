package cu.suitetecsa.sdk.android.balance;

import android.telephony.TelephonyManager;

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;
import cu.suitetecsa.sdk.android.balance.response.UssdResponse;

/**
 * Interfaz de devolución de llamada para la ejecución de solicitudes de saldo USSD
 */
public interface RequestCallback {
    /**
     * Método para obtener la instancia de TelephonyManager asociada con la solicitud de saldo USSD
     * @return a TelephonyManager instance
     */
    TelephonyManager getTelephonyManager();

    /**
     * Método de devolución de llamada invocado cuando se realiza una solicitud de saldo USSD
     * @param ussdRequest a UssdRequest instance
     */
    void onRequesting(UssdRequest ussdRequest);

    /**
     * Método de devolución de llamada invocado cuando una solicitud de saldo USSD tiene éxito
     *
     * @param request a UssdRequest instance
     * @param response a UssdResponse instance
     */
    void onSuccess(UssdRequest request, UssdResponse response);

    /**
     * Método de devolución de llamada invocado cuando una solicitud de saldo USSD falla
     * @param throwable a Throwable instance
     */
    void onFailure(Throwable throwable);
}
