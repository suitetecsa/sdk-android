package io.github.suitetecsa.sdk.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.SimCardCollectScope

@SuppressLint("MissingPermission")
class SimCardCollectorApi26(private val context: Context) : SimCardCollector {
    private fun getIccId(subscribedNetwork: SubscriptionInfo, manage: TelephonyManager?) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) subscribedNetwork.iccId else manage?.subscriberId

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.READ_PHONE_NUMBERS)
    private fun getPhoneNumber(info: SubscriptionInfo, manager: SubscriptionManager) =
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            info.number
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            manager.getPhoneNumber(info.subscriptionId)
        } else {
            null
        }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.READ_PHONE_NUMBERS)
    private fun mapSimCard(scope: SimCardCollectScope) = SimCard(
        getIccId(scope.subscribedNetwork!!, scope.telephonyManager),
        scope.subscribedNetwork!!.displayName.toString(),
        getPhoneNumber(scope.subscribedNetwork!!, scope.manager),
        scope.subscribedNetwork!!.simSlotIndex,
        scope.subscribedNetwork!!.subscriptionId,
        scope.telephonyManager
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    override fun collect(): List<SimCard> {
        val simCards: MutableList<SimCard> = ArrayList()
        val subscriptionManager = SimCardCollectorLegacy.getSubscriptionManager(context)
        if (subscriptionManager.activeSubscriptionInfoList.isNotEmpty()) {
            simCards.addAll(
                SimCardCollectorLegacy.getSimCards(
                    context,
                    subscriptionManager,
                    ::mapSimCard
                )
            )
        } else {
            simCards.addAll(
                SimCardCollectorLegacy.getSimCards(
                    context,
                    SimCardCollectorLegacy.getSubscriptionManagerForNetMonster(context),
                    ::mapSimCard
                )
            )
        }
        return simCards
    }
}
