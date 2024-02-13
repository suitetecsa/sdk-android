package cu.suitetecsa.sdk.android;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

import cu.suitetecsa.sdk.android.model.SimCard;
import cz.mroczis.netmonster.core.factory.NetMonsterFactory;

/**
 * Implementation of the SimCardCollector interface for collecting SIM card information.
 */
class SimCardCollectorImpl implements SimCardCollector {
    private final Context context;

    SimCardCollectorImpl(Context context) {
        this.context = context;
    }

    /**
     * Retrieves the list of SubscriptionInfo objects for active subscriptions.
     *
     * @param context The application context.
     * @return The list of SubscriptionInfo objects.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private List<SubscriptionInfo> getSubscriptionInfoList(@NonNull Context context) {
        SubscriptionManager subscriptionManager = ((SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE));
        return subscriptionManager.getActiveSubscriptionInfoList();
    }

    /**
     * Converts a list of SubscriptionInfo objects to a list of SimCard objects.
     *
     * @param subscriptionInfoList The list of SubscriptionInfo objects.
     * @return The list of SimCard objects.
     */
    @NonNull
    private List<SimCard> getSimCardsFromSubscriptionInfoList(@NonNull List<SubscriptionInfo> subscriptionInfoList) {
        List<SimCard> simCards = new ArrayList<>();
        for (SubscriptionInfo subscribedNetwork : subscriptionInfoList) {
            TelephonyManager telephonyManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    telephonyManager = telephonyManager.createForSubscriptionId(subscribedNetwork.getSubscriptionId());
                }
            }
            simCards.add(new SimCard(
                    String.valueOf(subscribedNetwork.getSubscriptionId()),
                    "",
                    subscribedNetwork.getSimSlotIndex(),
                    subscribedNetwork.getSubscriptionId(),
                    telephonyManager
            ));
        }
        return simCards;
    }

    /**
     * Retrieves the list of SimCard objects using NetMonster library.
     *
     * @param context The application context.
     * @return The list of SimCard objects.
     */
    @NonNull
    @RequiresPermission(
            allOf = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE}
    )
    private List<SimCard> getSimCardsFromNetMonster(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) NetMonsterFactory.INSTANCE.getSubscription(context);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        return getSimCardsFromSubscriptionInfoList(subscriptionInfoList);
    }

    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */
    @RequiresPermission(
            allOf = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE}
    )
    @Override
    public List<SimCard> collect() {
        List<SimCard> simCards = new ArrayList<>();

        List<SubscriptionInfo> subscriptionInfoList = getSubscriptionInfoList(context);
        if (!subscriptionInfoList.isEmpty()) {
            simCards.addAll(getSimCardsFromSubscriptionInfoList(subscriptionInfoList));
        } else {
            simCards.addAll(getSimCardsFromNetMonster(context));
        }

        return simCards;
    }
}
