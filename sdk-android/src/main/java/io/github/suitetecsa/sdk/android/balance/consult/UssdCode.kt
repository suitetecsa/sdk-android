package io.github.suitetecsa.sdk.android.balance.consult

import androidx.core.net.toUri

object UssdCode {
    val BALANCE = "*222" + "#".toUri()
    val DATA = "*222*328" + "#".toUri()
    val VOICE = "*222*869" + "#".toUri()
    val SMS = "*222*767" + "#".toUri()
    val BONUSES = "*222*266" + "#".toUri()
}