package cu.suitetecsa.sdk.android.balance.response

import cu.suitetecsa.sdk.android.model.DailyData
import cu.suitetecsa.sdk.android.model.MailData

/**
 * Clase para representar la respuesta de saldo de datos
 */
@JvmRecord
data class DataBalance(
    val usageBasedPricing: Boolean,
    val data: Long?,
    val dataLte: Long?,
    val remainingDays: Int?,
    val dailyData: DailyData?,
    val mailData: MailData?
) : UssdResponse
