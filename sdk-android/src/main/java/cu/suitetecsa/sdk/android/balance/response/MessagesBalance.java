package cu.suitetecsa.sdk.android.balance.response;

/**
 * Clase para representar la respuesta de saldo de mensajes
 */
public record MessagesBalance(long sms, Integer remainingDays) implements UssdResponse {
}
