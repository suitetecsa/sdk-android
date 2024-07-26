package io.github.suitetecsa.sdk.android

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import cz.mroczis.netmonster.core.factory.NetMonsterFactory.getSubscription
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.SimCardCollectScope

/**
 * Implementation of the SimCardCollector interface for collecting SIM card information.
 */
internal class SimCardCollectorLegacy(private val context: Context) : SimCardCollector {
    private fun mapSimCard(scope: SimCardCollectScope) = SimCard(
        null,
        scope.subscribedNetwork!!.displayName.toString(),
        null,
        scope.subscribedNetwork!!.simSlotIndex,
        scope.subscribedNetwork!!.subscriptionId,
        scope.telephonyManager
    )

    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    override fun collect(): List<SimCard> {
        val simCards: MutableList<SimCard> = ArrayList()
        val subscriptionManager = getSubscriptionManager(context)
        if (subscriptionManager.activeSubscriptionInfoList.isNotEmpty()) {
            simCards.addAll(getSimCards(context, subscriptionManager, ::mapSimCard))
        } else {
            simCards.addAll(getSimCards(context, getSubscriptionManagerForNetMonster(context), ::mapSimCard))
        }
        return simCards
    }

    companion object {
        @JvmStatic
        fun getSubscriptionManager(context: Context) =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        @JvmStatic
        fun getSubscriptionManagerForNetMonster(context: Context) =
            getSubscription(context) as SubscriptionManager

        @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
        @JvmStatic
        fun getSimCards(
            context: Context,
            manager: SubscriptionManager,
            block: SimCardCollectScope.() -> SimCard
        ): List<SimCard> {
            val simCards: MutableList<SimCard> = ArrayList()
            val scope = SimCardCollectScope(manager)
            for (subscribedNetwork in manager.activeSubscriptionInfoList) {
                scope.subscribedNetwork = subscribedNetwork
                var telephonyManager: TelephonyManager? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    telephonyManager = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                        .createForSubscriptionId(subscribedNetwork.subscriptionId)
                }
                scope.telephonyManager = telephonyManager
                simCards.add(scope.block())
            }
            return simCards
        }
    }
}
