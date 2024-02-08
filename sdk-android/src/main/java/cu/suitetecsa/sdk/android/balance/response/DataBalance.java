package cu.suitetecsa.sdk.android.balance.response;

import cu.suitetecsa.sdk.android.model.DailyData;
import cu.suitetecsa.sdk.android.model.MailData;

/**
 * Clase para representar la respuesta de saldo de datos
 */
public record DataBalance(boolean usageBasedPricing, Long data, Long dataLte, Integer remainingDays,
                          DailyData dailyData, MailData mailData) implements UssdResponse {
}
