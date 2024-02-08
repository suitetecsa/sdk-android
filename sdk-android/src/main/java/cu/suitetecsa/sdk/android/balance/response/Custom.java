package cu.suitetecsa.sdk.android.balance.response;

/**
 * Clase para representar una respuesta USSD personalizada
 */
public class Custom implements UssdResponse {
    private final String response;

    public Custom(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
