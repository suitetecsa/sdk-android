package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.balance.response.BonusBalance
import io.github.suitetecsa.sdk.android.model.BonusCredit
import io.github.suitetecsa.sdk.android.model.BonusData
import io.github.suitetecsa.sdk.android.model.BonusUnlimitedData
import io.github.suitetecsa.sdk.android.model.DataCu
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice

object BonusBalanceParser {
    /**
     * Parses the input text to extract bonus balance information, including credit, data, and specific data bonuses.
     *
     * @param input CharSequence containing the text to parse.
     * @return BonusBalance object containing parsed bonus credit, data, and other specific bonuses.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun extractBonusBalance(input: CharSequence): BonusBalance {
        return BonusBalance(
            input.extractCredit(),
            input.extractUnlimitedData(),
            input.extractData(),
            input.extractDataCu(),
            input.extractVoice(),
            input.extractSms()
        )
    }
}

val CharSequence.asBonusBalance: BonusBalance
    @RequiresApi(Build.VERSION_CODES.O)
    get() = BonusBalance(
        this.extractCredit(),
        this.extractUnlimitedData(),
        this.extractData(),
        this.extractDataCu(),
        this.extractVoice(),
        this.extractSms()
    )

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractCredit() =
    """\$(?<data>([\d.]+))\s+vence\s+(?<expires>(\d{2}-\d{2}-\d{2}))\.""".toRegex().find(this)
        ?.let { BonusCredit(it.groups["data"]!!.value, it.groups["expires"]!!.value) }

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractData(): BonusData? {
    val dataCountPattern = """(\d+(\.\d+)?)(\s)*([GMK])?B"""
    val dataCountGroup = """(?<data>$dataCountPattern)"""
    val dataCountLtePattern = """(?<dataLte>$dataCountPattern)\s+LTE"""
    val dataDueDatePattern = """(?<expires>(\d{2}-\d{2}-\d{2}))"""
    val fullDataCountPattern =
        """($dataCountGroup)?(\s+)?(\+)?(\s+)?($dataCountLtePattern)?\s+vence\s+$dataDueDatePattern(\.)?"""
    val unlimitedDataPattern = """ilimitados\s+vence\s+(?<unlimitedData>(\d{2}-\d{2}-\d{2}))\."""
    val dataRegex =
        """Datos:\s+($unlimitedDataPattern)?(\s+)?($fullDataCountPattern)?""".toRegex()

    return dataRegex.find(this)?.let {
        BonusData(it.groups["data"]?.value, it.groups["dataLte"]?.value, it.groups["expires"]!!.value)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractUnlimitedData() =
    """ilimitados\s+vence\s+(?<expires>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let { BonusUnlimitedData(it.groups["expires"]?.value!!) }

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractDataCu() =
    """Datos\.cu\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?\s+vence\s+(?<expires>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            DataCu(it.groups["data"]!!.value, it.groups["expires"]!!.value)
        }

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractVoice() =
    """Voz:\s+(?<data>(\d{2}:\d{2}:\d{2}))\s+vence\s+(?<expires>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            Voice(it.groups["data"]!!.value, it.groups["expires"]!!.value)
        }

@RequiresApi(Build.VERSION_CODES.O)
private fun CharSequence.extractSms() =
    """SMS:\s+(?<data>(\d+))\s+vence\s+(?<expires>(\d{2}-\d{2}-\d{2}))\.""".toRegex().find(this)
        ?.let { Sms(it.groups["data"]!!.value, it.groups["expires"]!!.value) }
