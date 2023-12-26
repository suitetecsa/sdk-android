package cu.suitetecsa.sdk.android.framework

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.domain.model.SimCard
import cz.mroczis.netmonster.core.factory.NetMonsterFactory

private const val TAG = "SimCardCollectorImpl"

/**
 * Implementation of the SimCardCollector interface for collecting SIM card information.
 *
 * @param context The application context.
 */
internal class SimCardCollectorImpl(
    private val context: Context
) : SimCardCollector {

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
        val simCards = mutableListOf<SimCard>()

        val subscriptionInfoList = getSubscriptionInfoList(context)
        if (subscriptionInfoList.isNotEmpty()) {
            simCards.addAll(getSimCardsFromSubscriptionInfoList(subscriptionInfoList))
        } else {
            simCards.addAll(getSimCardsFromNetMonster(context))
        }

        Log.e(TAG, "getSimCards: ${simCards.size}")
        return simCards
    }

    /**
     * Retrieves the list of SubscriptionInfo objects for active subscriptions.
     *
     * @param context The application context.
     * @return The list of SubscriptionInfo objects.
     */
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    private fun getSubscriptionInfoList(context: Context): List<SubscriptionInfo> {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        return subscriptionManager.activeSubscriptionInfoList ?: emptyList()
    }

    /**
     * Converts a list of SubscriptionInfo objects to a list of SimCard objects.
     *
     * @param subscriptionInfoList The list of SubscriptionInfo objects.
     * @return The list of SimCard objects.
     */
    private fun getSimCardsFromSubscriptionInfoList(subscriptionInfoList: List<SubscriptionInfo>): List<SimCard> {
        return subscriptionInfoList.map { info ->
            SimCard(
                serialNumber = info.iccId,
                displayName = info.displayName.toString(),
                slotIndex = info.simSlotIndex,
                subscriptionId = info.subscriptionId,
                telephony = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                        .createForSubscriptionId(info.subscriptionId)
                } else {
                    null
                }
            )
        }
    }

    /**
     * Retrieves the list of SimCard objects using NetMonster library.
     *
     * @param context The application context.
     * @return The list of SimCard objects.
     */
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    private fun getSimCardsFromNetMonster(context: Context): List<SimCard> {
        val subscriptionManager = NetMonsterFactory.getSubscription(context)
        val subscriptionInfoList = subscriptionManager.getActiveSubscriptions()

        return subscriptionInfoList.map { subscribedNetwork ->
            SimCard(
                serialNumber = subscribedNetwork.subscriptionId.toString(),
                displayName = "",
                slotIndex = subscribedNetwork.simSlotIndex,
                subscriptionId = subscribedNetwork.subscriptionId,
                telephony = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                        .createForSubscriptionId(subscribedNetwork.subscriptionId)
                } else {
                    null
                }
            )
        }
    }
}
