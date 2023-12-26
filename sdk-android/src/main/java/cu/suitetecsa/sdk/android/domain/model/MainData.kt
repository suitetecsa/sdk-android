package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the main data balance and related information for a USSD balance request.
 *
 * @param usageBasedPricing Indicates whether the data balance is subject to usage-based pricing.
 * @param data The main data balance in megabytes (MB).
 * @param dataLte The main LTE data balance in megabytes (MB).
 * @param remainingDays The remaining days for the data balance.
 */
data class MainData(
    val usageBasedPricing: Boolean,
    val data: Double?,
    val dataLte: Double?,
    val remainingDays: Int?
)
