package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainBalanceParser.extractMainBalance
import io.github.suitetecsa.sdk.android.model.MainBalance
import io.github.suitetecsa.sdk.android.utils.asBytes
import io.github.suitetecsa.sdk.android.utils.asDate
import io.github.suitetecsa.sdk.android.utils.asSeconds
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainBalanceParserTest : StringSpec({
    "should parse only main balance" {
        extractMainBalance("Saldo: 123.45 CUP. Linea activa hasta 15-08-24 vence 30-09-24.") shouldBe MainBalance(
            123.45f,
            null,
            null,
            null,
            null,
            "15-08-24".asDate!!,
            "30-09-24".asDate!!
        )
    }
    "should parse main balance with data" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Datos: 1.5 GB. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            123.45f,
            "1.5 GB".asBytes,
            null,
            null,
            null,
            "15-08-24".asDate!!,
            "30-09-24".asDate!!
        )
    }
    "should parse main balance with voice" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Voz: 12:34:56. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            123.45f,
            null,
            "12:34:56".asSeconds,
            null,
            null,
            "15-08-24".asDate!!,
            "30-09-24".asDate!!
        )
    }
    "should parse main balance with sms" {
        extractMainBalance(
            "Saldo: 123.45 CUP. SMS: 50. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            123.45f,
            null,
            null,
            50,
            null,
            "15-08-24".asDate!!,
            "30-09-24".asDate!!
        )
    }
    "should parse main balance with combine plan" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Datos: 1.5 GB. " +
                "Voz: 12:34:56. SMS: 50. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            123.45f,
            "1.5 GB".asBytes,
            "12:34:56".asSeconds,
            50,
            null,
            "15-08-24".asDate!!,
            "30-09-24".asDate!!
        )
    }
})
