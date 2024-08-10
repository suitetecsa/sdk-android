package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.model.DailyData
import io.github.suitetecsa.sdk.android.model.MailData

/**
 * Clase para representar la respuesta de saldo de datos
 */
@JvmRecord
data class DataBalance(
    val usageBasedPricing: Boolean,
    val data: String?,
    val dataLte: String?,
    val expires: String?,
    val dailyData: DailyData?,
    val mailData: MailData?
) : UssdResponse
