package cu.suitetecsa.sdk.android.domain.model

import android.telephony.TelephonyManager

/**
 * Represents a SIM card installed on the device.
 *
 * @property serialNumber The serial number of the SIM card.
 * @property displayName The display name of the SIM card.
 * @property slotIndex The index of the SIM card slot.
 * @property subscriptionId The subscription ID of the SIM card.
 * @property telephony The TelephonyManager instance associated with the SIM card.
 */
data class SimCard(
    val serialNumber: String,
    val displayName: String,
    val slotIndex: Int,
    val subscriptionId: Int,
    val telephony: TelephonyManager?
)
