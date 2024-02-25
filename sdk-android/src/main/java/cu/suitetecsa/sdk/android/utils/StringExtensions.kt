package cu.suitetecsa.sdk.android.utils

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.balance.parser.BonusBalanceParser
import cu.suitetecsa.sdk.android.balance.parser.MainBalanceParser
import cu.suitetecsa.sdk.android.balance.parser.MainDataParser
import cu.suitetecsa.sdk.android.balance.parser.MainSmsParser
import cu.suitetecsa.sdk.android.balance.parser.MainVoiceParser

@RequiresApi(Build.VERSION_CODES.O)
fun String.extractMainBalance() = MainBalanceParser.extractMainBalance(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.extractMainData() = MainDataParser.extractMainData(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.extractVoice() = MainVoiceParser.extractVoice(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.extractSms() = MainSmsParser.extractSms(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.extractBonusBalance() = BonusBalanceParser.extractBonusBalance(this)
