package io.github.suitetecsa.sdk.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import cz.mroczis.netmonster.core.factory.NetMonsterFactory.getSubscription
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.SimCardCollectScope

/**
 * Implementation of [SimCardCollector] to collect SIM card data.
 */
@SuppressLint("MissingPermission")
class SimCardCollectorImpl(private val context: Context) : SimCardCollector {

    /**
     * Retrieves the phone number associated with a given SubscriptionInfo.
     *
     * @param subscriptionInfo The SubscriptionInfo object.
     * @param subscriptionManager The SubscriptionManager instance.
     * @return The phone number or null if not available.
     */
    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.READ_PHONE_NUMBERS)
    private fun getPhoneNumber(subscriptionInfo: SubscriptionInfo, subscriptionManager: SubscriptionManager) =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                subscriptionManager.getPhoneNumber(subscriptionInfo.subscriptionId)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                subscriptionInfo.number
            else -> null
        }

    /**
     * Maps a [SimCardCollectScope] object to a [SimCard] object.
     *
     * @param scope The [SimCardCollectScope] object.
     * @return A [SimCard] object with information from the scope.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_NUMBERS)
    private fun mapToSimCard(scope: SimCardCollectScope) = SimCard(
        displayName = scope.subscribedNetwork.displayName.toString(),
        phoneNumber = getPhoneNumber(scope.subscribedNetwork, scope.manager),
        slotIndex = scope.subscribedNetwork.simSlotIndex,
        subscriptionId = scope.subscribedNetwork.subscriptionId,
        telephony = scope.telephonyManager
    )

    /**
     * Retrieves the subscription manager.
     *
     * @param context The context.
     * @return The SubscriptionManager instance.
     */
    private fun getSubscriptionManager(context: Context) =
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    /**
     * Retrieves the subscription manager using `getSubscription`.
     *
     * @param context The context.
     * @return The SubscriptionManager instance.
     */
    private fun getSubscriptionManagerForNetMonster(context: Context) =
        getSubscription(context) as SubscriptionManager

    /**
     * Retrieves the list of SIM cards.
     *
     * @param context The context.
     * @param manager The SubscriptionManager instance.
     * @param mapper A function to map a [SimCardCollectScope] to a [SimCard].
     * @return A list of [SimCard] objects.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun getSimCards(
        context: Context,
        manager: SubscriptionManager,
        mapper: (SimCardCollectScope) -> SimCard
    ) = manager.activeSubscriptionInfoList?.map { subscriptionInfo ->
        val telephonyManager = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
            .createForSubscriptionId(subscriptionInfo.subscriptionId)
        val scope = SimCardCollectScope(
            subscribedNetwork = subscriptionInfo,
            manager = manager,
            telephonyManager = telephonyManager
        )
        mapper(scope)
    } ?: emptyList()

    /**
     * Collects the list of SIM cards available on the device.
     *
     * @return A list of [SimCard] objects.
     */
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    override fun collect(): List<SimCard> {
        val subscriptionManager = getSubscriptionManager(context)
        return subscriptionManager.activeSubscriptionInfoList?.let { activeSubscriptions ->
            if (activeSubscriptions.isNotEmpty()) {
                getSimCards(context, subscriptionManager, ::mapToSimCard)
            } else {
                getSimCards(context, getSubscriptionManagerForNetMonster(context), ::mapToSimCard)
            }
        } ?: emptyList()
    }
}
