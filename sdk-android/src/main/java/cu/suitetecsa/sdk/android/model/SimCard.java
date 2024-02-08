package cu.suitetecsa.sdk.android.model;

import android.telephony.TelephonyManager;

public record SimCard(String serialNumber, String displayName, int slotIndex, int subscriptionId,
                      TelephonyManager telephony) {
}
