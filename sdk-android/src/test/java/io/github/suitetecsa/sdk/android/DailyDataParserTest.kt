package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.DailyDataParser.parseDailyData
import io.github.suitetecsa.sdk.android.model.DailyData
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DailyDataParserTest : StringSpec({
    "should parse daily data" {
        parseDailyData("Diaria: 100 MB validos 24 horas.") shouldBe DailyData("100 MB", "24")
    }
    "should parse daily data when not active" {
        parseDailyData("Diaria: 100 MB no activos.") shouldBe DailyData("100 MB", "no activos")
    }
})
