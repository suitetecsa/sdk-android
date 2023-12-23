package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.domain.model.BonusCredit
import cu.suitetecsa.sdk.android.domain.model.BonusData
import cu.suitetecsa.sdk.android.domain.model.BonusDataCU
import cu.suitetecsa.sdk.android.domain.model.BonusUnlimitedData
import cu.suitetecsa.sdk.android.framework.UssdResponse
import cu.suitetecsa.sdk.android.framework.toBytes

/**
 * Parses the bonus balance from a given CharSequence and returns a UssdResponse.BonusBalance object.
 *
 * @return The parsed bonus balance as a UssdResponse.BonusBalance object.
 */
fun CharSequence.parseBonusBalance(): UssdResponse.BonusBalance {
    // Regular expressions to match the bonus credit, bonus data, and bonus data CU patterns
    val creditRegex =
        """\$(?<bonusCredit>([\d.]+))\s+vence\s+(?<bonusCreditDueDate>(\d{2}-\d{2}-\d{2}))\."""
            .toRegex()
    val dataRegex =
        (
            """Datos:\s+(ilimitados\s+vence\s+(?<unlimitedData>(\d{2}-\d{2}-\d{2}))\.)?""" +
                """(\s+)?((?<dataAllNetworkBonus>(\d+(\.\d+)?)(\s)*([GMK])?B))?(\s+\+\s+)?""" +
                """((?<bonusDataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+vence\s+)?""" +
                """((?<bonusDataDueDate>(\d{2}-\d{2}-\d{2}))\.)?"""
            )
            .toRegex()
    val dataCURegex =
        (
            """Datos\.cu\s+(?<bonusDataCu>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+vence\s+)?""" +
                """(?<bonusDataCuDueDate>(\d{2}-\d{2}-\d{2}))?\."""
            )
            .toRegex()

    // Parse bonus credit
    val bonusCredit = creditRegex.find(this)?.let { matchResult ->
        BonusCredit(
            credit = matchResult.groups["bonusCredit"]?.value?.toFloatOrNull()!!,
            bonusCreditDueDate = matchResult.groups["bonusCreditDueDate"]?.value!!
        )
    }

    // Parse bonus data and bonus unlimited data
    val (bonusData, bonusUnlimitedData) = dataRegex.find(this)?.let { dataMatcher ->
        Pair(
            BonusData(
                bonusDataCount = dataMatcher.groups["dataAllNetworkBonus"]?.value?.toBytes(),
                bonusDataCountLte = dataMatcher.groups["bonusDataLte"]?.value?.toBytes(),
                bonusDataDueDate = dataMatcher.groups["bonusDataDueDate"]?.value ?: ""
            ),
            dataMatcher.groups["unlimitedData"]?.value?.let {
                BonusUnlimitedData(
                    bonusUnlimitedDataDueDate = it
                )
            }
        )
    } ?: Pair(null, null)

    // Parse bonus data CU
    val bonusDataCU = dataCURegex.find(this)?.let { dataCUMatcher ->
        BonusDataCU(
            bonusDataCuCount = dataCUMatcher.groups["bonusDataCu"]?.value?.toBytes()!!,
            bonusDataCuDueDate = dataCUMatcher.groups["bonusDataCuDueDate"]?.value!!
        )
    }

    // Return the parsed bonus balance
    return UssdResponse.BonusBalance(
        credit = bonusCredit,
        data = bonusData,
        dataCu = bonusDataCU,
        unlimitedData = bonusUnlimitedData
    )
}
