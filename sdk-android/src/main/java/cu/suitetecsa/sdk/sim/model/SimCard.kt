package cu.suitetecsa.sdk.sim.model

import android.telephony.TelephonyManager

data class SimCard(
    val serialNumber: String,
    val displayName: String,
    val slotIndex: Int,
    val subscriptionId: Int,
    val telephony: TelephonyManager
)
