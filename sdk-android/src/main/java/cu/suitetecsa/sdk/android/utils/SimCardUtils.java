package cu.suitetecsa.sdk.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import cu.suitetecsa.sdk.android.balance.FetchBalanceCallBack;
import cu.suitetecsa.sdk.android.balance.RequestCallback;
import cu.suitetecsa.sdk.android.balance.UssdRequestSender;
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;
import cu.suitetecsa.sdk.android.balance.response.UssdResponse;
import cu.suitetecsa.sdk.android.model.SimCard;

public class SimCardUtils {
    public static void makeCall(SimCard simCard, Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phoneNumber));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", String.valueOf(simCard.subscriptionId()));
        } else {
            intent.putExtra("con.android.phone.extra.PHONE_ACCOUNT_HANDLE", String.valueOf(simCard.subscriptionId()));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    public static void smartFetchBalance(SimCard simCard, FetchBalanceCallBack callBack) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public TelephonyManager getTelephonyManager() {
                return simCard.telephony();
            }

            @Override
            public void onRequesting(UssdRequest request) {
                callBack.onFetching(request);
            }

            @Override
            public void onSuccess(UssdRequest request, UssdResponse response) {
                callBack.onSuccess(request, response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callBack.onFailure(throwable);
            }
        };
        new UssdRequestSender.Builder().build().send(requestCallback);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    public static void ussdFetch(SimCard simCard, String ussdCode, FetchBalanceCallBack callBack) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public TelephonyManager getTelephonyManager() {
                return simCard.telephony();
            }

            @Override
            public void onRequesting(UssdRequest request) {
                callBack.onFetching(request);
            }

            @Override
            public void onSuccess(UssdRequest request, UssdResponse response) {
                callBack.onSuccess(request, response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callBack.onFailure(throwable);
            }
        };
        new UssdRequestSender.Builder().build().send(ussdCode, requestCallback);
    }
}
