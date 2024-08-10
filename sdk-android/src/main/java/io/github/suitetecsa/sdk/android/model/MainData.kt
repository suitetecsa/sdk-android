package io.github.suitetecsa.sdk.android.model

@JvmRecord
data class MainData(
    @JvmField val consumptionRate: Boolean,
    @JvmField val data: String?,
    @JvmField val dataLte: String?,
    @JvmField val expires: String?
)
