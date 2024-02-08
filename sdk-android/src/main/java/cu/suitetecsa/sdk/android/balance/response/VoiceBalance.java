package cu.suitetecsa.sdk.android.balance.response;

/**
 * Clase para representar la respuesta de saldo de voz
 */
public class VoiceBalance implements UssdResponse {
    private final long time;
    private final Integer remainingDays;

    public VoiceBalance(long time, Integer remainingDays) {
        this.time = time;
        this.remainingDays = remainingDays;
    }

    public long getTime() {
        return time;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }
}
