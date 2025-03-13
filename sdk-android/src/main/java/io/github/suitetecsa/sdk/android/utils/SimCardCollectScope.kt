package io.github.suitetecsa.sdk.android.utils

import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

/**
 * Data class to hold the information needed for collecting SIM card data.
 * @property subscribedNetwork Subscription information for the current SIM.
 * @property manager Subscription manager instance.
 * @property telephonyManager Telephony manager instance for the current SIM.
 */
data class SimCardCollectScope(
    var subscribedNetwork: SubscriptionInfo,
    val manager: SubscriptionManager,
    var telephonyManager: TelephonyManager
)
