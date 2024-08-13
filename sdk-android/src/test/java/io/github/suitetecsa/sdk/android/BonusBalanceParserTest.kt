package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.BonusBalanceParser.extractBonusBalance
import io.github.suitetecsa.sdk.android.balance.response.BonusBalance
import io.github.suitetecsa.sdk.android.model.BonusCredit
import io.github.suitetecsa.sdk.android.model.BonusData
import io.github.suitetecsa.sdk.android.model.BonusUnlimitedData
import io.github.suitetecsa.sdk.android.model.DataCu
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BonusBalanceParserTest : StringSpec({
    "should parse bonus balance with all possible bonuses" {
        extractBonusBalance(
            "$10.50 vence 15-08-24. " +
                "Datos: ilimitados vence 15-08-24. 500 MB + 200 MB LTE vence 20-08-24. " +
                "Datos.cu 300 MB vence 20-08-24. " +
                "Voz: 12:34:56 vence 18-08-24. " +
                "SMS: 50 vence 25-08-24."
        ) shouldBe BonusBalance(
            BonusCredit("10.50", "15-08-24"),
            BonusUnlimitedData("15-08-24"),
            BonusData("500 MB", "200 MB", "20-08-24"),
            DataCu("300 MB", "20-08-24"),
            Voice("12:34:56", "18-08-24"),
            Sms("50", "25-08-24")
        )
    }
    "should parse bonus balance with no data bonuses" {
        extractBonusBalance(
            "$10.50 vence 15-08-24. " +
                "Datos: ilimitados vence 15-08-24. " +
                "Datos.cu 300 MB vence 20-08-24. " +
                "Voz: 12:34:56 vence 18-08-24. " +
                "SMS: 50 vence 25-08-24."
        ) shouldBe BonusBalance(
            BonusCredit("10.50", "15-08-24"),
            BonusUnlimitedData("15-08-24"),
            null,
            DataCu("300 MB", "20-08-24"),
            Voice("12:34:56", "18-08-24"),
            Sms("50", "25-08-24")
        )
    }
    "should parse bonus balance with no unlimited data bonus" {
        extractBonusBalance(
            "$10.50 vence 15-08-24. " +
                "Datos: 500 MB + 200 MB LTE vence 20-08-24. " +
                "Datos.cu 300 MB vence 20-08-24. " +
                "Voz: 12:34:56 vence 18-08-24. " +
                "SMS: 50 vence 25-08-24."
        ) shouldBe BonusBalance(
            BonusCredit("10.50", "15-08-24"),
            null,
            BonusData("500 MB", "200 MB", "20-08-24"),
            DataCu("300 MB", "20-08-24"),
            Voice("12:34:56", "18-08-24"),
            Sms("50", "25-08-24")
        )
    }
    "should parse bonus balance with no unlimited data and no data bonuses" {
        extractBonusBalance(
            "$10.50 vence 15-08-24. " +
                "Datos.cu 300 MB vence 20-08-24. " +
                "Voz: 12:34:56 vence 18-08-24. " +
                "SMS: 50 vence 25-08-24."
        ) shouldBe BonusBalance(
            BonusCredit("10.50", "15-08-24"),
            null,
            null,
            DataCu("300 MB", "20-08-24"),
            Voice("12:34:56", "18-08-24"),
            Sms("50", "25-08-24")
        )
    }
    "should parse bonus balance with credit, dataCu and voice bonuses" {
        extractBonusBalance(
            "$10.50 vence 15-08-24. " +
                "Datos.cu 300 MB vence 20-08-24. " +
                "Voz: 12:34:56 vence 18-08-24."
        ) shouldBe BonusBalance(
            BonusCredit("10.50", "15-08-24"),
            null,
            null,
            DataCu("300 MB", "20-08-24"),
            Voice("12:34:56", "18-08-24"),
            null
        )
    }
    "should parse bonus balance with dataCu" {
        extractBonusBalance(
            "Datos.cu 300 MB vence 20-08-24."
        ) shouldBe BonusBalance(
            null,
            null,
            null,
            DataCu("300 MB", "20-08-24"),
            null,
            null
        )
    }
})
