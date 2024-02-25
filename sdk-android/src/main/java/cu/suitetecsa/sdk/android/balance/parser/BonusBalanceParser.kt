package cu.suitetecsa.sdk.android.balance.parser

import cu.suitetecsa.sdk.android.balance.response.BonusBalance
import cu.suitetecsa.sdk.android.model.BonusCredit
import cu.suitetecsa.sdk.android.model.BonusData
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData
import cu.suitetecsa.sdk.android.model.DataCu
import cu.suitetecsa.sdk.android.model.Sms
import cu.suitetecsa.sdk.android.model.Voice
import cu.suitetecsa.sdk.android.utils.LongUtils.toRemainingDays
import cu.suitetecsa.sdk.android.utils.StringUtils
import cu.suitetecsa.sdk.android.utils.StringUtils.toBytes
import cu.suitetecsa.sdk.android.utils.StringUtils.toDateMillis

object BonusBalanceParser {
    /**
     * Parses the input text to extract bonus balance information, including credit, data, and specific data bonuses.
     *
     * @param input CharSequence containing the text to parse.
     * @return BonusBalance object containing parsed bonus credit, data, and other specific bonuses.
     */
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

val CharSequence.asBonusBalance: BonusBalance?
    get() = BonusBalance(
        this.extractCredit(),
        this.extractUnlimitedData(),
        this.extractData(),
        this.extractDataCu(),
        this.extractVoice(),
        this.extractSms()
    )

private fun CharSequence.extractCredit() =
    """\$(?<volume>([\d.]+))\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\.""".toRegex().find(this)?.let {
        BonusCredit(
            it.groups["volume"]!!.value.toDouble(),
            toRemainingDays(toDateMillis(it.groups["dueDate"]!!.value))
        )
    }

private fun CharSequence.extractData(): BonusData? {
    val dataCountPattern = """(\d+(\.\d+)?)(\s)*([GMK])?B"""
    val dataCountGroup = """(?<volume>${dataCountPattern})"""
    val dataCountLtePattern = """(?<volumeLte>${dataCountPattern})\s+LTE"""
    val dataDueDatePattern = """(?<dueDate>(\d{2}-\d{2}-\d{2}))"""
    val fullDataCountPattern =
        """(${dataCountGroup})?(\s+)?(\+)?(\s+)?(${dataCountLtePattern})?\s+vence\s+${dataDueDatePattern}(\.)?"""
    val unlimitedDataPattern = """ilimitados\s+vence\s+(?<unlimitedData>(\d{2}-\d{2}-\d{2}))\."""
    val dataRegex = """Datos:\s+($unlimitedDataPattern)?(\s+)?($fullDataCountPattern)?""".toRegex()

    return dataRegex.find(this)?.let {
        val volume = it.groups["volume"]?.value
        val volumeLte = it.groups["volumeLte"]?.value
        val dueDate = it.groups["dueDate"]!!.value
        BonusData(
            volume?.let { dv -> toBytes(dv) },
            volumeLte?.let { dlv -> toBytes(dlv) },
            toRemainingDays(toDateMillis(dueDate))
        )
    }
}

private fun CharSequence.extractUnlimitedData() =
    """ilimitados\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            it.groups["dueDate"]?.value?.let { ud ->
                BonusUnlimitedData(toRemainingDays(toDateMillis(ud)))
            }
        }

private fun CharSequence.extractDataCu() =
    """Datos\.cu\s+(?<volume>(\d+(\.\d+)?)(\s)*([GMK])?B)?\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            DataCu(
                toBytes(it.groups["volume"]!!.value),
                toRemainingDays(toDateMillis(it.groups["dueDate"]!!.value))
            )
        }

private fun CharSequence.extractVoice() =
    """Voz:\s+(?<volume>(\d{2}:\d{2}:\d{2}))\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            Voice(
                StringUtils.toSeconds(it.groups["volume"]!!.value),
                toRemainingDays(toDateMillis(it.groups["dueDate"]!!.value))
            )
        }

private fun CharSequence.extractSms() =
    """SMS:\s+(?<volume>(\d+))\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
        .toRegex().find(this)?.let {
            Sms(it.groups["volume"]!!.value.toLong(), toRemainingDays(toDateMillis(it.groups["dueDate"]!!.value)))
        }
