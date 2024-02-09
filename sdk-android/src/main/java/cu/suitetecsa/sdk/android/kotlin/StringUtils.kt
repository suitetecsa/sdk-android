package cu.suitetecsa.sdk.android.kotlin

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.balance.parser.BonusBalanceParser
import cu.suitetecsa.sdk.android.balance.parser.MainBalanceParser
import cu.suitetecsa.sdk.android.balance.parser.MainDataParser
import cu.suitetecsa.sdk.android.balance.parser.MainSmsParser
import cu.suitetecsa.sdk.android.balance.parser.MainVoiceParser

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseMainBalance() = MainBalanceParser.parseMainBalance(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseMainData() = MainDataParser.parseMainData(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseMainVoice() = MainVoiceParser.parseMainVoice(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseMainSms() = MainSmsParser.parseMainSms(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseBonusBalance() = BonusBalanceParser.parseBonusBalance(this)
