package cu.suitetecsa.sdk.android.framework

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.framework.extensions.parseBonusBalance
import cu.suitetecsa.sdk.android.framework.extensions.parseDailyData
import cu.suitetecsa.sdk.android.framework.extensions.parseMailData
import cu.suitetecsa.sdk.android.framework.extensions.parseMainBalance
import cu.suitetecsa.sdk.android.framework.extensions.parseMainData
import cu.suitetecsa.sdk.android.framework.extensions.parseMainSms
import cu.suitetecsa.sdk.android.framework.extensions.parseMainVoice
import java.util.LinkedList
import java.util.Queue

private const val DELAY_MS = 2000L

/**
 * Object that implements the logic for executing USSD requests.
 *
 * This class is responsible for sending USSD requests and processing the received responses.
 * It uses the CALL_PHONE permission to make the requests.
 */
object UssdBalanceCommandExecutor : UssdBalanceRequestExecutor {

    /**
     * Executes a USSD balance request.
     *
     * This method initializes the USSD request and prepares the queue of consult types.
     * It requires the CALL_PHONE permission to function correctly.
     *
     * @param callback The callback interface to handle USSD responses.
     * @throws SecurityException if the CALL_PHONE permission is not granted.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    override fun execute(callback: UssdBalanceRequestExecutorCallBack) {
        val requestsTypes = LinkedList<UssdConsultType>()
        requestsTypes.offer(UssdConsultType.PrincipalBalance)
        Handler(Looper.getMainLooper()).postDelayed({ sendRequest(callback, requestsTypes) }, DELAY_MS)
    }

    /**
     * Executes a USSD balance request.
     *
     * This method initializes the USSD request and prepares the queue of consult types.
     * It requires the CALL_PHONE permission to function correctly.
     *
     * @param callback The callback interface to handle USSD responses.
     * @throws SecurityException if the CALL_PHONE permission is not granted.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    override fun execute(ussdCode: String, callback: UssdBalanceRequestExecutorCallBack) {
        val requestsTypes = LinkedList<UssdConsultType>()
        requestsTypes.offer(UssdConsultType.Custom(ussdCode))
        Handler(Looper.getMainLooper()).postDelayed({ sendRequest(callback, requestsTypes) }, DELAY_MS)
    }

    /**
     * Sends a USSD request and processes the response.
     *
     * This method sends the USSD request and handles the response through the callback.
     * It requires the CALL_PHONE permission to function correctly.
     *
     * @param callback The callback interface to handle USSD responses.
     * @param requestsTypes The queue of USSD consult types.
     * @throws SecurityException if the CALL_PHONE permission is not granted.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    private fun sendRequest(
        callback: UssdBalanceRequestExecutorCallBack,
        requestsTypes: Queue<UssdConsultType>
    ) {
        requestsTypes.poll()?.also {
            callback.onRequesting(it)
            callback.telephonyManager.sendUssdRequest(
                it.ussdCode,
                object : TelephonyManager.UssdResponseCallback() {
                    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        when (it) {
                            UssdConsultType.PrincipalBalance -> {
                                response.parseMainBalance()?.let { balance ->
                                    balance.data?.also { requestsTypes.offer(UssdConsultType.DataBalance) }
                                    balance.voice?.also { requestsTypes.offer(UssdConsultType.VoiceBalance) }
                                    balance.sms?.also { requestsTypes.offer(UssdConsultType.MessagesBalance) }
                                    callback.onSuccess(
                                        UssdResponse.PrincipalBalance(
                                            balance.credit,
                                            balance.activeUntil,
                                            balance.mainBalanceDueDate,
                                            requestsTypes.toList()
                                        )
                                    )
                                    requestsTypes.offer(UssdConsultType.BonusBalance)
                                } ?: run {
                                    callback.onFailure(UssdRequestException(response.toString()))
                                }
                            }
                            UssdConsultType.DataBalance -> {
                                response.parseMainData()?.let { mainData ->
                                    callback.onSuccess(
                                        UssdResponse.DataBalance(
                                            usageBasedPricing = mainData.usageBasedPricing,
                                            data = mainData.data,
                                            dataLte = mainData.dataLte,
                                            remainingDays = mainData.remainingDays,
                                            dailyData = response.parseDailyData(),
                                            mailData = response.parseMailData()
                                        )
                                    )
                                } ?: run {
                                    callback.onFailure(UssdRequestException(response.toString()))
                                }
                            }
                            UssdConsultType.VoiceBalance -> {
                                response.parseMainVoice()?.let { voiceBalance ->
                                    callback.onSuccess(voiceBalance)
                                } ?: run {
                                    callback.onFailure(UssdRequestException(response.toString()))
                                }
                            }
                            UssdConsultType.MessagesBalance -> {
                                response.parseMainSms()?.let { messagesBalance ->
                                    callback.onSuccess(messagesBalance)
                                } ?: run {
                                    callback.onFailure(UssdRequestException(response.toString()))
                                }
                            }
                            UssdConsultType.BonusBalance -> {
                                callback.onSuccess(response.parseBonusBalance())
                            }
                            is UssdConsultType.Custom -> {
                                callback.onSuccess(UssdResponse.Custom(response.toString()))
                            }
                        }
                        Handler(Looper.getMainLooper()).postDelayed({ sendRequest(callback, requestsTypes) }, DELAY_MS)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }
}
