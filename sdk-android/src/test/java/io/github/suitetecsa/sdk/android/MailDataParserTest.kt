package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MailDataParser.parseMailData
import io.github.suitetecsa.sdk.android.model.MailData
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MailDataParserTest : StringSpec({
    "should parse mail data" {
        parseMailData("Mensajeria: 1.5 MB validos 30 dias.") shouldBe MailData("1.5 MB", "30")
    }
    "should parse mail data when not active" {
        parseMailData("Mensajeria: 1.5 MB no activos.") shouldBe MailData("1.5 MB", "no activos")
    }
})
