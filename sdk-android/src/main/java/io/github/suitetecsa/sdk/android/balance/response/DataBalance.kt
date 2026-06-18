package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.model.DailyData

/**
 * Clase para representar la respuesta de saldo de datos
 */
@JvmRecord
data class DataBalance(
    val usageBasedPricing: Boolean,
    val data: Long?,
    val dataLte: Long?,
    val expires: String?,
    val dailyData: DailyData?
) : UssdResponse
