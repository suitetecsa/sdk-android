package cu.suitetecsa.sdk.android.balance

import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.exception.UssdRequestException
import cu.suitetecsa.sdk.android.balance.parser.BonusBalanceParser.extractBonusBalance
import cu.suitetecsa.sdk.android.balance.parser.DailyDataParser.parseDailyData
import cu.suitetecsa.sdk.android.balance.parser.MailDataParser.parseMailData
import cu.suitetecsa.sdk.android.balance.parser.MainBalanceParser.extractMainBalance
import cu.suitetecsa.sdk.android.balance.parser.MainDataParser.extractMainData
import cu.suitetecsa.sdk.android.balance.parser.MainSmsParser.extractSms
import cu.suitetecsa.sdk.android.balance.parser.MainVoiceParser.extractVoice
import cu.suitetecsa.sdk.android.balance.response.Custom
import cu.suitetecsa.sdk.android.balance.response.DataBalance
import cu.suitetecsa.sdk.android.balance.response.PrincipalBalance
import java.text.ParseException
import java.util.LinkedList
import java.util.Queue

internal class UssdRequestSenderImpl(private val delayMillis: Long) : UssdRequestSender {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(callback: RequestCallback) {
        val requestsTypes: Queue<UssdRequest> = LinkedList()
        requestsTypes.offer(UssdRequest.PRINCIPAL_BALANCE)
        Handler(Looper.getMainLooper()).postDelayed(
            { sendRequest(callback, requestsTypes, null) },
            delayMillis
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(ussdCode: String, callback: RequestCallback) {
        val requestsTypes: Queue<UssdRequest> = LinkedList()
        requestsTypes.offer(UssdRequest.CUSTOM)
        Handler(Looper.getMainLooper()).postDelayed({
            sendRequest(
                callback,
                requestsTypes,
                ussdCode
            )
        }, delayMillis)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun sendRequest(
        callback: RequestCallback,
        requestsTypes: Queue<UssdRequest>,
        ussdCode: String?
    ) {
        val ussdRequest = requestsTypes.poll()
        if (ussdRequest != null) {
            callback.onRequesting(ussdRequest)
            val code = if (ussdRequest == UssdRequest.CUSTOM) ussdCode else ussdRequest.ussdCode
            callback.telephonyManager?.sendUssdRequest(code, object : UssdResponseCallback() {
                @RequiresPermission(Manifest.permission.CALL_PHONE)
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    when (ussdRequest) {
                        UssdRequest.PRINCIPAL_BALANCE -> try {
                            val balance = extractMainBalance(response)
                            if (balance.data != null) requestsTypes.offer(UssdRequest.DATA_BALANCE)
                            if (balance.voice != null) requestsTypes.offer(UssdRequest.VOICE_BALANCE)
                            if (balance.sms != null) requestsTypes.offer(UssdRequest.MESSAGES_BALANCE)
                            requestsTypes.offer(UssdRequest.BONUS_BALANCE)
                            callback.onSuccess(
                                ussdRequest, PrincipalBalance(
                                    balance.balance,
                                    balance.activeUntil,
                                    balance.dueDate,
                                    ArrayList(requestsTypes)
                                )
                            )
                        } catch (e: ParseException) { callback.onFailure(e) }

                        UssdRequest.DATA_BALANCE -> {
                            val mainData = extractMainData(response)
                            try {
                                callback.onSuccess(
                                    ussdRequest, DataBalance(
                                        mainData.usageBasedPricing,
                                        mainData.data,
                                        mainData.dataLte,
                                        mainData.remainingDays,
                                        parseDailyData(response),
                                        parseMailData(response)
                                    )
                                )
                            } catch (e: ParseException) {
                                callback.onFailure(e)
                            }
                        }

                        UssdRequest.VOICE_BALANCE -> try {
                            callback.onSuccess(ussdRequest, extractVoice(response))
                        } catch (e: ParseException) {
                            callback.onFailure(e)
                        }

                        UssdRequest.MESSAGES_BALANCE -> try {
                            callback.onSuccess(ussdRequest, extractSms(response))
                        } catch (e: ParseException) {
                            callback.onFailure(e)
                        }

                        UssdRequest.BONUS_BALANCE -> callback.onSuccess(
                            ussdRequest,
                            extractBonusBalance(response)
                        )

                        else -> callback.onSuccess(ussdRequest, Custom(response.toString()))
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        sendRequest(
                            callback,
                            requestsTypes,
                            null
                        )
                    }, delayMillis)
                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) = callback.onFailure(UssdRequestException("Exception code :: $failureCode"))
            }, Handler(Looper.getMainLooper()))
        }
    }
}
