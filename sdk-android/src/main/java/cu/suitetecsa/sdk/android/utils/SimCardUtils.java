package cu.suitetecsa.sdk.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import cu.suitetecsa.sdk.android.balance.ConsultBalanceCallBack;
import cu.suitetecsa.sdk.android.model.SimCard;

public class SimCardUtils {
    public static void makeCall(SimCard simCard, Context context, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", String.valueOf(simCard.subscriptionId()));
        } else {
            intent.putExtra("con.android.phone.extra.PHONE_ACCOUNT_HANDLE", String.valueOf(simCard.subscriptionId()));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void ussdExecute(SimCard simCard, String ussdCode, ConsultBalanceCallBack callBack) {

    }
}
