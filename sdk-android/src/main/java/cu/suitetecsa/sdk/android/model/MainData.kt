package cu.suitetecsa.sdk.android.model

@JvmRecord
data class MainData(
    @JvmField val usageBasedPricing: Boolean,
    @JvmField val data: Long?,
    @JvmField val dataLte: Long?,
    @JvmField val remainingDays: Int?
)
