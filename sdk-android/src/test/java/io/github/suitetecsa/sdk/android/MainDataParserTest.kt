package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainDataParser.extractMainData
import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.utils.asBytes
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainDataParserTest : StringSpec({
    "should parse main data with consumptionRate off and data plan" {
        extractMainData("Tarifa: No activa. Paquetes: 2.5 GB validos 30 dias.") shouldBe MainData(
            false, "2.5 GB".asBytes, "30"
        )
    }
    "should parse main data with consumptionRate on and data plan" {
        extractMainData("Tarifa: Activa. Paquetes: 2.5 GB validos 30 dias.") shouldBe MainData(
            true, "2.5 GB".asBytes, "30"
        )
    }
    "should parse main data with consumptionRate on, without data" {
        extractMainData("Tarifa: Activa.") shouldBe MainData(true, null, null)
    }
})
