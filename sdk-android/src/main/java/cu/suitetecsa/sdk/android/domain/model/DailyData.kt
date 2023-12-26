package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the daily data balance and remaining hours for a USSD balance request.
 *
 * @param data The daily data balance.
 * @param remainingHours The remaining hours for the balance, or null if the balance is not time-based.
 */
data class DailyData(val data: Double, val remainingHours: Int?)
