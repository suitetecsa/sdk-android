package cu.suitetecsa.sdk.android.balance;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

/**
 * Interfaz para ejecutar una solicitud de saldo USSD
 */
public interface UssdRequestSender {
    /**
     * Método para ejecutar una solicitud de saldo USSD con el callback proporcionado
     * @param callback a RequestCallback instance
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    void send(RequestCallback callback);

    /**
     * Método para ejecutar una solicitud de saldo USSD con el código USSD y el callback proporcionados
     * @param ussdCode ussdCode to send request
     * @param callback a RequestCallback instance
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    void send(String ussdCode, RequestCallback callback);


    class Builder {
        private Long DELAY_MS;

        public Builder withDelay(long delay) {
            this.DELAY_MS = delay;
            return this;
        }

        public UssdRequestSender build() {
            return new UssdRequestSenderImpl(DELAY_MS != null ? DELAY_MS : 2000L);
        }
    }
}
