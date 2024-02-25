package cu.suitetecsa.sdk.android.model

import android.telephony.TelephonyManager

@JvmRecord
data class SimCard(
    val serialNumber: String,
    val displayName: String,
    val slotIndex: Int,
    @JvmField val subscriptionId: Int,
    @JvmField val telephony: TelephonyManager?
)