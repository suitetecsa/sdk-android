package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import io.github.suitetecsa.sdk.android.balance.exception.UssdRequestException
import io.github.suitetecsa.sdk.android.balance.parser.BonusBalanceParser.extractBonusBalance
import io.github.suitetecsa.sdk.android.balance.parser.DailyDataParser.parseDailyData
import io.github.suitetecsa.sdk.android.balance.parser.MailDataParser.parseMailData
import io.github.suitetecsa.sdk.android.balance.parser.MainBalanceParser.extractMainBalance
import io.github.suitetecsa.sdk.android.balance.parser.MainDataParser.extractMainData
import io.github.suitetecsa.sdk.android.balance.parser.MainSmsParser.extractSms
import io.github.suitetecsa.sdk.android.balance.parser.MainVoiceParser.extractVoice
import io.github.suitetecsa.sdk.android.balance.response.Custom
import io.github.suitetecsa.sdk.android.balance.response.DataBalance
import io.github.suitetecsa.sdk.android.balance.response.PrincipalBalance
import java.text.ParseException
import java.util.LinkedList
import java.util.Queue

internal class UssdRequestSenderImpl(private val delayMillis: Long) : UssdRequestSender {

    private val mainHandler = Handler(Looper.getMainLooper())

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(callback: RequestCallback) {
        val requestsQueue: Queue<UssdRequest> = LinkedList()
        requestsQueue.offer(UssdRequest.PRINCIPAL_BALANCE)
        mainHandler.postDelayed({
            sendRequest(callback, requestsQueue, null)
        }, delayMillis)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(ussdCode: String, callback: RequestCallback) {
        val requestsQueue: Queue<UssdRequest> = LinkedList()
        requestsQueue.offer(UssdRequest.CUSTOM)
        mainHandler.postDelayed({
            sendRequest(callback, requestsQueue, ussdCode)
        }, delayMillis)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun sendRequest(
        callback: RequestCallback,
        requestsQueue: Queue<UssdRequest>,
        ussdCode: String?
    ) {
        val currentRequest = requestsQueue.poll() ?: return

        callback.onRequesting(currentRequest)
        val code = if (currentRequest == UssdRequest.CUSTOM) ussdCode else currentRequest.ussdCode

        callback.telephonyManager?.sendUssdRequest(
            code,
            object : UssdResponseCallback() {
                @RequiresPermission(Manifest.permission.CALL_PHONE)
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    try {
                        handleUssdResponse(currentRequest, response, requestsQueue, callback)
                    } catch (e: ParseException) {
                        callback.onFailure(e)
                    }
                    mainHandler.postDelayed({
                        sendRequest(callback, requestsQueue, null)
                    }, delayMillis)
                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) = callback.onFailure(UssdRequestException("Exception code :: $failureCode"))
            },
            Handler(Looper.getMainLooper())
        )
    }

    private fun handleUssdResponse(
        currentRequest: UssdRequest,
        response: CharSequence,
        requestsQueue: Queue<UssdRequest>,
        callback: RequestCallback
    ) {
        when (currentRequest) {
            UssdRequest.PRINCIPAL_BALANCE -> {
                val balance = extractMainBalance(response)
                if (balance.data != null) requestsQueue.offer(UssdRequest.DATA_BALANCE)
                if (balance.voice != null) requestsQueue.offer(UssdRequest.VOICE_BALANCE)
                if (balance.sms != null) requestsQueue.offer(UssdRequest.MESSAGES_BALANCE)
                requestsQueue.offer(UssdRequest.BONUS_BALANCE)
                callback.onSuccess(
                    currentRequest,
                    PrincipalBalance(
                        balance.balance,
                        balance.lockDate,
                        balance.deletionDate,
                        ArrayList(requestsQueue)
                    )
                )
            }

            UssdRequest.DATA_BALANCE -> {
                val mainData = extractMainData(response)
                callback.onSuccess(
                    currentRequest,
                    DataBalance(
                        mainData.consumptionRate,
                        mainData.data,
                        mainData.dataLte,
                        mainData.expires,
                        parseDailyData(response),
                        parseMailData(response)
                    )
                )
            }

            UssdRequest.VOICE_BALANCE -> {
                callback.onSuccess(currentRequest, extractVoice(response))
            }

            UssdRequest.MESSAGES_BALANCE -> {
                callback.onSuccess(currentRequest, extractSms(response))
            }

            UssdRequest.BONUS_BALANCE -> {
                callback.onSuccess(currentRequest, extractBonusBalance(response))
            }

            else -> callback.onSuccess(currentRequest, Custom(response.toString()))
        }
    }
}
