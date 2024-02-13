package cu.suitetecsa.sdk.android.balance.response;

/**
 * Clase para representar la respuesta de saldo de voz
 */
public record VoiceBalance(long seconds, Integer remainingDays) implements UssdResponse {
}
