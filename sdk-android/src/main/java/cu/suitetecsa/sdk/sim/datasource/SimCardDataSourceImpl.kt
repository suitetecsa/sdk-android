package cu.suitetecsa.sdk.sim.datasource

import android.Manifest
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.sim.model.SimCard
import cz.mroczis.netmonster.core.factory.NetMonsterFactory

const val TAG = "SimCardDataSource"

internal class SimCardDataSourceImpl(
    private val context: Context
) : SimCardDataSource {

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    override fun getSimCards(): List<SimCard> {
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

    private fun getSimCardsFromSubscriptionInfoList(subscriptionInfoList: List<SubscriptionInfo>): List<SimCard> {
        return subscriptionInfoList.map {
            SimCard(
                it.iccId, it.displayName.toString(), it.simSlotIndex, it.subscriptionId
            )
        }
    }

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

        return subscriptionInfoList.map {
            SimCard(
                it.subscriptionId.toString(), "", it.simSlotIndex, it.subscriptionId
            )
        }
    }
}
