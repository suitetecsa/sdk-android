package io.github.suitetecsa.sdk.android.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.balance.SdkCallback
import io.github.suitetecsa.sdk.android.balance.UssdStringCallback
import io.github.suitetecsa.sdk.android.balance.consult.UssdCode
import io.github.suitetecsa.sdk.android.balance.parser.MainBalanceParser
import io.github.suitetecsa.sdk.android.balance.parser.MainDataParser
import io.github.suitetecsa.sdk.android.balance.parser.MainSmsParser
import io.github.suitetecsa.sdk.android.balance.parser.MainVoiceParser
import io.github.suitetecsa.sdk.android.balance.parser.BonusBalanceParser
import io.github.suitetecsa.sdk.android.balance.response.BonusBalance
import io.github.suitetecsa.sdk.android.balance.response.MessagesBalance
import io.github.suitetecsa.sdk.android.balance.response.VoiceBalance
import io.github.suitetecsa.sdk.android.model.MainBalance
import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.model.SimCard

object SimCardBalanceQueries {

    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun queryBalance(simCard: SimCard, callback: SdkCallback<MainBalance>) {
        simCard.ussdFetch(UssdCode.BALANCE, parseWrapper(callback, MainBalanceParser::extractMainBalance))
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun queryData(simCard: SimCard, callback: SdkCallback<MainData>) {
        simCard.ussdFetch(UssdCode.DATA, parseWrapper(callback, MainDataParser::extractMainData))
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun queryVoice(simCard: SimCard, callback: SdkCallback<VoiceBalance>) {
        simCard.ussdFetch(UssdCode.VOICE, parseWrapper(callback, MainVoiceParser::extractVoice))
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @RequiresApi(Build.VERSION_CODES.O)
    fun querySms(simCard: SimCard, callback: SdkCallback<MessagesBalance>) {
        simCard.ussdFetch(UssdCode.SMS, parseWrapper(callback, MainSmsParser::extractSms))
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @RequiresApi(Build.VERSION_CODES.O)
    fun queryBonuses(simCard: SimCard, callback: SdkCallback<BonusBalance>) {
        simCard.ussdFetch(UssdCode.BONUSES, parseWrapper(callback, BonusBalanceParser::extractBonusBalance))
    }
}

private fun <T> parseWrapper(
    callback: SdkCallback<T>,
    parser: (CharSequence) -> T,
): UssdStringCallback {
    return object : UssdStringCallback {
        override fun onSuccess(rawResponse: String) {
            try {
                callback.onSuccess(parser(rawResponse))
            } catch (e: Exception) {
                callback.onFailure(e)
            }
        }

        override fun onFailure(throwable: Throwable) {
            callback.onFailure(throwable)
        }
    }
}

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.queryBalance(callback: SdkCallback<MainBalance>) =
    SimCardBalanceQueries.queryBalance(this, callback)

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.queryData(callback: SdkCallback<MainData>) =
    SimCardBalanceQueries.queryData(this, callback)

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun SimCard.queryVoice(callback: SdkCallback<VoiceBalance>) =
    SimCardBalanceQueries.queryVoice(this, callback)

@RequiresPermission(Manifest.permission.CALL_PHONE)
@RequiresApi(Build.VERSION_CODES.O)
fun SimCard.querySms(callback: SdkCallback<MessagesBalance>) =
    SimCardBalanceQueries.querySms(this, callback)

@RequiresPermission(Manifest.permission.CALL_PHONE)
@RequiresApi(Build.VERSION_CODES.O)
fun SimCard.queryBonuses(callback: SdkCallback<BonusBalance>) =
    SimCardBalanceQueries.queryBonuses(this, callback)
