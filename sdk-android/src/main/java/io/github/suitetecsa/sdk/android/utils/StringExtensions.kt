package io.github.suitetecsa.sdk.android.utils

import io.github.suitetecsa.sdk.android.balance.parser.BonusBalanceParser
import io.github.suitetecsa.sdk.android.balance.parser.MainBalanceParser
import io.github.suitetecsa.sdk.android.balance.parser.MainDataParser
import io.github.suitetecsa.sdk.android.balance.parser.MainSmsParser
import io.github.suitetecsa.sdk.android.balance.parser.MainVoiceParser
import java.util.Date

fun String.extractMainBalance() = MainBalanceParser.extractMainBalance(this)

fun String.extractMainData() = MainDataParser.extractMainData(this)

fun String.extractVoice() = MainVoiceParser.extractVoice(this)

fun String.extractSms() = MainSmsParser.extractSms(this)

fun String.extractBonusBalance() = BonusBalanceParser.extractBonusBalance(this)

@Suppress("unused")
fun String.fixDate() = StringUtils.fixDateString(this)

@Suppress("unused")
val String.asSeconds: Long
    get() = StringUtils.toSeconds(this)

@Suppress("unused")
val String.asDateMillis: Long
    get() = StringUtils.toDateMillis(this)

@Suppress("unused")
val String.asDate: Date?
    get() = StringUtils.toDate(this)

@Suppress("unused")
val String.asBytes: Long
    get() = StringUtils.toBytes(this)

@Suppress("unused")
val String.isActive: Boolean
    get() = StringUtils.isActive(this)
