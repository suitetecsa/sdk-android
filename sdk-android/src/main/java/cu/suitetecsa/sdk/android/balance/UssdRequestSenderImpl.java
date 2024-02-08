package cu.suitetecsa.sdk.android.balance;

import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.BONUS_BALANCE;
import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.CUSTOM;
import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.DATA_BALANCE;
import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.MESSAGES_BALANCE;
import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.PRINCIPAL_BALANCE;
import static cu.suitetecsa.sdk.android.balance.consult.UssdRequest.VOICE_BALANCE;
import static cu.suitetecsa.sdk.android.balance.parser.BonusBalanceParser.parseBonusBalance;
import static cu.suitetecsa.sdk.android.balance.parser.DailyDataParser.parseDailyData;
import static cu.suitetecsa.sdk.android.balance.parser.MailDataParser.parseMailData;
import static cu.suitetecsa.sdk.android.balance.parser.MainBalanceParser.parseMainBalance;
import static cu.suitetecsa.sdk.android.balance.parser.MainDataParser.parseMainData;
import static cu.suitetecsa.sdk.android.balance.parser.MainSmsParser.parseMainSms;
import static cu.suitetecsa.sdk.android.balance.parser.MainVoiceParser.parseMainVoice;

import android.Manifest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;
import cu.suitetecsa.sdk.android.balance.exception.UssdRequestException;
import cu.suitetecsa.sdk.android.balance.response.Custom;
import cu.suitetecsa.sdk.android.balance.response.DataBalance;
import cu.suitetecsa.sdk.android.balance.response.PrincipalBalance;
import cu.suitetecsa.sdk.android.model.MainBalance;
import cu.suitetecsa.sdk.android.model.MainData;

class UssdRequestSenderImpl implements UssdRequestSender {

    private final long DELAY_MS;

    UssdRequestSenderImpl(long delay) {
        this.DELAY_MS = delay;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @Override
    public void send(RequestCallback callback) {
        Queue<UssdRequest> requestsTypes = new LinkedList<>();
        requestsTypes.offer(PRINCIPAL_BALANCE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> sendRequest(callback, requestsTypes, null), DELAY_MS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @Override
    public void send(@NonNull String ussdCode, RequestCallback callback) {
        Queue<UssdRequest> requestsTypes = new LinkedList<>();
        requestsTypes.offer(CUSTOM);
        new Handler(Looper.getMainLooper()).postDelayed(() -> sendRequest(callback, requestsTypes, ussdCode), DELAY_MS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private void sendRequest(RequestCallback callback, @NonNull Queue<UssdRequest> requestsTypes, String ussdCode) {
        UssdRequest ussdRequest = requestsTypes.poll();
        if (ussdRequest != null) {
            callback.onRequesting(ussdRequest);
            String code = ussdRequest == UssdRequest.CUSTOM ? ussdCode : ussdRequest.getUssdCode();
            callback.getTelephonyManager().sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                @RequiresPermission(Manifest.permission.CALL_PHONE)
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    switch (ussdRequest) {
                        case PRINCIPAL_BALANCE:
                            try {
                                MainBalance balance = parseMainBalance(response);
                                if (balance == null) callback.onFailure(new UssdRequestException(response.toString()));
                                else {
                                    if (balance.data() != null)
                                        requestsTypes.offer(DATA_BALANCE);
                                    if (balance.voice() != null)
                                        requestsTypes.offer(VOICE_BALANCE);
                                    if (balance.sms() != null)
                                        requestsTypes.offer(MESSAGES_BALANCE);
                                    requestsTypes.offer(BONUS_BALANCE);

                                    callback.onSuccess(ussdRequest, new PrincipalBalance(
                                            balance.balance(),
                                            balance.activeUntil(),
                                            balance.dueDate(),
                                            new ArrayList<>(requestsTypes)
                                    ));
                                }
                            } catch (ParseException e) {
                                callback.onFailure(e);
                            }
                            break;
                        case DATA_BALANCE:
                            MainData data = parseMainData(response);
                            try {
                                callback.onSuccess(ussdRequest, new DataBalance(
                                        data.usageBasedPricing(), data.data(), data.dataLte(), data.remainingDays(), parseDailyData(response), parseMailData(response)
                                ));
                            } catch (ParseException e) {
                                callback.onFailure(e);
                            }
                            break;
                        case VOICE_BALANCE:
                            try {
                                callback.onSuccess(ussdRequest, parseMainVoice(response));
                            } catch (ParseException e) {
                                callback.onFailure(e);
                            }
                            break;
                        case MESSAGES_BALANCE:
                            try {
                                callback.onSuccess(ussdRequest, parseMainSms(response));
                            } catch (ParseException e) {
                                callback.onFailure(e);
                            }
                            break;
                        case BONUS_BALANCE:
                            try {
                                callback.onSuccess(ussdRequest, parseBonusBalance(response));
                            } catch (ParseException e) {
                                callback.onFailure(e);
                            }
                            break;
                        default:
                            callback.onSuccess(ussdRequest, new Custom(response.toString()));
                            break;
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(() -> sendRequest(callback, requestsTypes, null), DELAY_MS);
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    callback.onFailure(new UssdRequestException("Exception code :: " + failureCode));
                }
            }, new Handler(Looper.getMainLooper()));
        }
    }
}
