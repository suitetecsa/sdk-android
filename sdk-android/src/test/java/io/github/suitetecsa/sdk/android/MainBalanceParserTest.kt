package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainBalanceParser.extractMainBalance
import io.github.suitetecsa.sdk.android.model.MainBalance
import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainBalanceParserTest : StringSpec({
    "should parse only main balance" {
        extractMainBalance("Saldo: 123.45 CUP. Linea activa hasta 15-08-24 vence 30-09-24.") shouldBe MainBalance(
            "123.45",
            null,
            null,
            null,
            null,
            null,
            "15-08-24",
            "30-09-24"
        )
    }
    "should parse main balance with data" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Datos: 1.5 GB + 500 MB LTE. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            "123.45",
            MainData(false, "1.5 GB", "500 MB", "no activos"),
            null,
            null,
            null,
            null,
            "15-08-24",
            "30-09-24"
        )
    }
    "should parse main balance with voice" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Voz: 12:34:56. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            "123.45",
            null,
            Voice("12:34:56", "no activos"),
            null,
            null,
            null,
            "15-08-24",
            "30-09-24"
        )
    }
    "should parse main balance with sms" {
        extractMainBalance(
            "Saldo: 123.45 CUP. SMS: 50. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            "123.45",
            null,
            null,
            Sms("50", "no activos"),
            null,
            null,
            "15-08-24",
            "30-09-24"
        )
    }
    "should parse main balance with combine plan" {
        extractMainBalance(
            "Saldo: 123.45 CUP. Datos: 1.5 GB + 500 MB LTE. " +
                "Voz: 12:34:56. SMS: 50. " +
                "Linea activa hasta 15-08-24 vence 30-09-24."
        ) shouldBe MainBalance(
            "123.45",
            MainData(false, "1.5 GB", "500 MB", "no activos"),
            Voice("12:34:56", "no activos"),
            Sms("50", "no activos"),
            null,
            null,
            "15-08-24",
            "30-09-24"
        )
    }
})
