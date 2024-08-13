package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainVoiceParser.extractVoice
import io.github.suitetecsa.sdk.android.balance.response.VoiceBalance
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainVoiceParserTest : StringSpec({
    "should parse main voice balance" {
        extractVoice("Usted dispone de 10:30:45 MIN validos por 7 dias.") shouldBe VoiceBalance(
            "10:30:45", "7"
        )
    }
})
