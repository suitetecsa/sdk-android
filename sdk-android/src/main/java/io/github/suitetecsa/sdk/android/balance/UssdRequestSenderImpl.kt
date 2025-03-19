package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
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
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse
import java.text.ParseException
import java.util.LinkedList
import java.util.Queue

class UssdRequestSenderImpl(
    private val initialDelayMillis: Long,
    private val maxRetries: Int,
    private val retryDelayMillis: Long
) : UssdRequestSender {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val requestsQueue: Queue<RetryableRequest> = LinkedList()

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(callback: RequestCallback) {
        requestsQueue.clear()
        requestsQueue.offer(RetryableRequest(UssdRequest.PRINCIPAL_BALANCE))
        scheduleRequest(callback, initialDelayMillis)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun send(ussdCode: String, callback: RequestCallback) {
        requestsQueue.clear()
        requestsQueue.offer(RetryableRequest(UssdRequest.CUSTOM, ussdCode))
        scheduleRequest(callback, initialDelayMillis)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun scheduleRequest(callback: RequestCallback, delay: Long) {
        mainHandler.postDelayed({
            processNextRequest(callback)
        }, delay)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun processNextRequest(callback: RequestCallback) {
        val currentRequest = requestsQueue.peek() ?: return

        callback.onStateChanged(currentRequest.request, RequestState.STARTED, currentRequest.retryCount)
        executeRequest(currentRequest, callback)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun executeRequest(currentRequest: RetryableRequest, callback: RequestCallback) {
        val code = when (currentRequest.request) {
            UssdRequest.CUSTOM -> currentRequest.customCode
            else -> currentRequest.request.ussdCode
        } ?: return

        callback.telephonyManager?.sendUssdRequest(
            code,
            object : TelephonyManager.UssdResponseCallback() {
                @RequiresPermission(Manifest.permission.CALL_PHONE)
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    try {
                        handleSuccessResponse(currentRequest, response, callback)
                    } catch (e: ParseException) {
                        handleFailure(currentRequest, callback, e)
                    }
                }

                @RequiresPermission(Manifest.permission.CALL_PHONE)
                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    handleFailure(
                        currentRequest,
                        callback,
                        UssdRequestException("Failed with code: $failureCode")
                    )
                }
            },
            mainHandler
        )
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun handleSuccessResponse(
        currentRequest: RetryableRequest,
        response: CharSequence,
        callback: RequestCallback
    ) {
        callback.onStateChanged(currentRequest.request, RequestState.SUCCEEDED, currentRequest.retryCount)
        val result = processResponse(currentRequest.request, response)
        callback.onSuccess(currentRequest.request, result)

        requestsQueue.poll()

        processNextRequest(callback)
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun handleFailure(
        currentRequest: RetryableRequest,
        callback: RequestCallback,
        error: Throwable
    ) {
        if (currentRequest.retryCount < maxRetries) {
            currentRequest.retryCount++
            callback.onStateChanged(
                currentRequest.request,
                RequestState.RETRYING,
                currentRequest.retryCount
            )
            scheduleRequest(callback, retryDelayMillis)
        } else {
            callback.onStateChanged(
                currentRequest.request,
                RequestState.FAILED,
                currentRequest.retryCount
            )
            callback.onFailure(error)
        }
    }

    private fun processResponse(request: UssdRequest, response: CharSequence): UssdResponse {
        return when (request) {
            UssdRequest.PRINCIPAL_BALANCE -> {
                val balance = extractMainBalance(response)

                if (balance.data != null) requestsQueue.add(RetryableRequest(UssdRequest.DATA_BALANCE))
                if (balance.voice != null) requestsQueue.add(RetryableRequest(UssdRequest.VOICE_BALANCE))
                if (balance.sms != null) requestsQueue.add(RetryableRequest(UssdRequest.MESSAGES_BALANCE))
                requestsQueue.add(RetryableRequest(UssdRequest.BONUS_BALANCE))

                PrincipalBalance(
                    balance.balance,
                    balance.lockDate,
                    balance.deletionDate,
                    ArrayList(requestsQueue.map { it.request })
                )
            }
            UssdRequest.DATA_BALANCE -> {
                val mainData = extractMainData(response)
                DataBalance(
                    mainData.consumptionRate,
                    mainData.data,
                    mainData.dataLte,
                    mainData.expires,
                    parseDailyData(response),
                    parseMailData(response)
                )
            }
            UssdRequest.VOICE_BALANCE -> extractVoice(response)
            UssdRequest.MESSAGES_BALANCE -> extractSms(response)
            UssdRequest.BONUS_BALANCE -> extractBonusBalance(response)
            else -> Custom(response.toString())
        }
    }
}
