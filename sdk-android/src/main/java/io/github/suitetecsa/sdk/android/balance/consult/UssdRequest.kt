package io.github.suitetecsa.sdk.android.balance.consult

import androidx.core.net.toUri

enum class UssdRequest(val ussdCode: String) {
    BALANCE("*222" + "#".toUri()),
    DATA("*222*328" + "#".toUri()),
    VOICE("*222*869" + "#".toUri()),
    SMS("*222*767" + "#".toUri()),
    BONUSES("*222*266" + "#".toUri()),
    CUSTOM("Nothing")
}
