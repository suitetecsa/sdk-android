package io.github.suitetecsa.sdk.android.utils

import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

class SimCardCollectScope(val manager: SubscriptionManager) {
    var subscribedNetwork: SubscriptionInfo? = null
    lateinit var telephonyManager: TelephonyManager
}
