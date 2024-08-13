package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainSmsParser.extractSms
import io.github.suitetecsa.sdk.android.balance.response.MessagesBalance
import io.github.suitetecsa.sdk.android.utils.extractSms
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainSmsParserTest : StringSpec({
    "should parse main SMS balance" {
        extractSms("Usted dispone de 50 SMS validos por 30 dias.") shouldBe MessagesBalance("50 SMS", "30")
    }
    "should parse main SMS balance when not active" {
        extractSms("Usted dispone de 50 SMS no activos.") shouldBe MessagesBalance("50 SMS", "no activos")
    }
})
