package cu.suitetecsa.sdk.ussd.model

data class MainData(
    val usageBasedPricing: Boolean,
    val data: Double?,
    val dataLte: Double?,
    val remainingDays: Int?
)
