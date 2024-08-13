package io.github.suitetecsa.sdk.android

import io.github.suitetecsa.sdk.android.balance.parser.MainDataParser.extractMainData
import io.github.suitetecsa.sdk.android.model.MainData
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MainDataParserTest : StringSpec({
    "should parse main data with consumptionRate off, 3G and 4G" {
        extractMainData("Tarifa: No activa. Paquetes: 2.5 GB + 1 GB LTE validos 30 dias.") shouldBe MainData(
            false, "2.5 GB", "1 GB", "30"
        )
    }
    "should parse main data with consumptionRate on and 3G" {
        extractMainData("Tarifa: Activa. Paquetes: 2.5 GB validos 30 dias.") shouldBe MainData(
            true, "2.5 GB", null, "30"
        )
    }
    "should parse main data with consumptionRate on and 4G" {
        extractMainData("Tarifa: Activa. Paquetes: 1 GB LTE validos 30 dias.") shouldBe MainData(
            true, null, "1 GB", "30"
        )
    }
    "should parse main data with consumptionRate on, without 3G and 4G" {
        extractMainData("Tarifa: Activa.") shouldBe MainData(true, null, null, null)
    }
})
