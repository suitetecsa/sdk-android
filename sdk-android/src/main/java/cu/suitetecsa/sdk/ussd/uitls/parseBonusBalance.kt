package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseBonusBalance(): BonusBalance {
    val creditRegex =
        """\$(?<bonusCredit>([\d.]+))\s+vence\s+(?<bonusCreditDueDate>(\d{2}-\d{2}-\d{2}))\."""
            .toRegex()
    val dataRegex =
        ("""Datos:\s+(ilimitados\s+vence\s+(?<unlimitedData>(\d{2}-\d{2}-\d{2}))\.)?""" +
                """(\s+)?((?<dataAllNetworkBonus>(\d+(\.\d+)?)(\s)*([GMK])?B))?(\s+\+\s+)?""" +
                """((?<bonusDataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+vence\s+)?""" +
                """((?<bonusDataDueDate>(\d{2}-\d{2}-\d{2}))\.)?""")
            .toRegex()
    val dataCURegex =
        ("""Datos\.cu\s+(?<bonusDataCu>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+vence\s+)?""" +
            """(?<bonusDataCuDueDate>(\d{2}-\d{2}-\d{2}))?\.""")
            .toRegex()


    val bonusCredit = creditRegex.find(this.message)?.let { matchResult ->
        BonusCredit(
            credit = matchResult.groups["bonusCredit"]?.value?.toFloatOrNull(),
            bonusCreditDueDate = matchResult.groups["bonusCreditDueDate"]?.value
        )
    } ?: BonusCredit(credit = null, bonusCreditDueDate = null)

    val (bonusData, bonusUnlimitedData) = dataRegex.find(this.message)?.let { dataMatcher ->
        Pair(
            BonusData(
                bonusDataCount = dataMatcher.groups["dataAllNetworkBonus"]?.value?.toBytes()?.toLong()
                    ?: 0L,
                bonusDataCountLte = dataMatcher.groups["bonusDataLte"]?.value?.toBytes()?.toLong() ?: 0L,
                bonusDataDueDate = dataMatcher.groups["bonusDataDueDate"]?.value ?: ""
            ),
            BonusUnlimitedData(
                bonusUnlimitedDataDueDate = dataMatcher.groups["unlimitedData"]?.value
            )
        )
    } ?: Pair(
        BonusData(bonusDataCount = null, bonusDataCountLte = null, bonusDataDueDate = null),
        BonusUnlimitedData(bonusUnlimitedDataDueDate = null)
    )

    val bonusDataCU = dataCURegex.find(this.message)?.let { dataCUMatcher ->
        BonusDataCU(
            bonusDataCuCount = dataCUMatcher.groups["bonusDataCu"]?.value?.toBytes()?.toLong(),
            bonusDataCuDueDate = dataCUMatcher.groups["bonusDataCuDueDate"]?.value
        )
    } ?: BonusDataCU(bonusDataCuCount = null, bonusDataCuDueDate = null)

    return BonusBalance(
        credit = bonusCredit,
        data = bonusData,
        dataCu = bonusDataCU,
        unlimitedData = bonusUnlimitedData
    )
}