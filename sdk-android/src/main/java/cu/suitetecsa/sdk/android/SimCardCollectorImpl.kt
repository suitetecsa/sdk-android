package cu.suitetecsa.sdk.android

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.model.SimCard
import cz.mroczis.netmonster.core.factory.NetMonsterFactory.getSubscription

/**
 * Implementation of the SimCardCollector interface for collecting SIM card information.
 */
internal class SimCardCollectorImpl(private val context: Context) : SimCardCollector {
    /**
     * Retrieves the list of SubscriptionInfo objects for active subscriptions.
     *
     * @param context The application context.
     * @return The list of SubscriptionInfo objects.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun getSubscriptionInfoList(context: Context): List<SubscriptionInfo> {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        return subscriptionManager.activeSubscriptionInfoList
    }

    /**
     * Converts a list of SubscriptionInfo objects to a list of SimCard objects.
     *
     * @param subscriptionInfoList The list of SubscriptionInfo objects.
     * @return The list of SimCard objects.
     */
    private fun getSimCardsFromSubscriptionInfoList(subscriptionInfoList: List<SubscriptionInfo>): List<SimCard> {
        val simCards: MutableList<SimCard> = ArrayList()
        for (subscribedNetwork in subscriptionInfoList) {
            var telephonyManager: TelephonyManager? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager =
                    telephonyManager.createForSubscriptionId(subscribedNetwork.subscriptionId)
            }
            simCards.add(
                SimCard(
                    subscribedNetwork.subscriptionId.toString(),
                    "",
                    subscribedNetwork.simSlotIndex,
                    subscribedNetwork.subscriptionId,
                    telephonyManager
                )
            )
        }
        return simCards
    }

    /**
     * Retrieves the list of SimCard objects using NetMonster library.
     *
     * @param context The application context.
     * @return The list of SimCard objects.
     */
    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    ])
    private fun getSimCardsFromNetMonster(context: Context): List<SimCard> {
        val subscriptionManager = getSubscription(context) as SubscriptionManager
        val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        return getSimCardsFromSubscriptionInfoList(subscriptionInfoList)
    }

    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */
    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    ])
    override fun collect(): List<SimCard> {
        val simCards: MutableList<SimCard> = ArrayList()
        val subscriptionInfoList = getSubscriptionInfoList(
            context
        )
        if (subscriptionInfoList.isNotEmpty()) {
            simCards.addAll(getSimCardsFromSubscriptionInfoList(subscriptionInfoList))
        } else {
            simCards.addAll(getSimCardsFromNetMonster(context))
        }
        return simCards
    }
}
