package io.github.suitetecsa.sdk.android.utils

import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

class SimCardCollectScope(val manager: SubscriptionManager) {
    lateinit var subscribedNetwork: SubscriptionInfo
    lateinit var telephonyManager: TelephonyManager
}
