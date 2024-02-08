package cu.suitetecsa.sdk.android.balance;

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;
import cu.suitetecsa.sdk.android.balance.response.UssdResponse;

/**
 * Callback interface for USSD balance requests.
 * <p>
 * This interface defines three callback functions that handle the different stages of a USSD balance request:
 * - `onRequesting`: Called when a USSD request is being sent.
 * - `onSuccess`: Called when a USSD response is received successfully.
 * - `onFailure`: Called when an error occurs during the USSD request.
 */
public interface ConsultBalanceCallBack {
    /**
     * Called when a USSD request is being sent.
     *
     * @param request The type of USSD consult being requested.
     */
    void onRequesting(UssdRequest request);

    /**
     * Called when a USSD response is received successfully.
     *
     * @param request The type of USSD consult being requested.
     * @param response The USSD response received.
     */
    void onSuccess(UssdRequest request, UssdResponse response);

    /**
     * Called when an error occurs during the USSD request.
     *
     * @param throwable The throwable representing the error.
     */
    void onFailure(Throwable throwable);
}
