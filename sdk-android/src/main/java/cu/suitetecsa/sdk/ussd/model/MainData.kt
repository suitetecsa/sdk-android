package cu.suitetecsa.sdk.ussd.model

data class MainData(
    val usageBasedPricing: Boolean,
    val mainData: Long,
    val mainDataLte: Long,
    val mainDataDueDate: String
)
