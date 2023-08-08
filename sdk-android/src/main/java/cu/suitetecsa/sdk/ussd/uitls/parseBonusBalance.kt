package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseBonusBalance(): BonusBalance {
    val creditPattern =
        Pattern.compile("""(\$(?<bonusCredit>([\d.]+))\s+vence\s+(?<bonusCreditDueDate>(\d{2}-\d{2}-\d{2}))\.)?""")
    val dataPattern =
        Pattern.compile("""(Datos:\s+(ilimitados\s+vence\s+)?(?<unlimitedData>(\d{2}-\d{2}-\d{2})\.)?"""
                    + """(\s+)?(?<dataAllNetworkBonus>(\d+(\.\d+)?)(\s)*([GMK])?B)?"""
                    + """(\s+\+\s+)?((?<bonusDataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+vence\s+)?"""
                    + """(?<bonusDataDueDate>(\d{2}-\d{2}-\d{2}))?\.)?""")
    val dataCUPattern =
        Pattern.compile("""(Datos\.cu\s+(?<bonusDataCu>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+vence\s+)?"""
                    + """(?<bonusDataCuDueDate>(\d{2}-\d{2}-\d{2}))?\.)?""")


    val creditMatcher = creditPattern.matcher(this.message)
    val bonusCredit = if (creditMatcher.find()) {
        BonusCredit(
            credit = creditMatcher.group("bonusCredit")?.toFloatOrNull() ?: 0f,
            bonusCreditDueDate = creditMatcher.group("bonusCreditDueDate") ?: ""
        )
    } else BonusCredit(credit = 0f, bonusCreditDueDate = "")

    val dataMatcher = dataPattern.matcher(this.message)
    val (bonusData, bonusUnlimitedData) = if (dataMatcher.find()) {
        Pair(
            BonusData(
                bonusDataCount = dataMatcher.group("dataAllNetworkBonus")?.toBytes()?.toLong()
                    ?: 0L,
                bonusDataCountLte = dataMatcher.group("bonusDataLte")?.toBytes()?.toLong() ?: 0L,
                bonusDataDueDate = dataMatcher.group("bonusDataDueDate") ?: ""
            ),
            BonusUnlimitedData(
                bonusUnlimitedDataDueDate = dataMatcher.group("unlimitedData") ?: ""
            )
        )
    } else Pair(
        BonusData(bonusDataCount = 0L, bonusDataCountLte = 0L, bonusDataDueDate = ""),
        BonusUnlimitedData(bonusUnlimitedDataDueDate = "")
    )

    val dataCUMatcher = dataCUPattern.matcher(this.message)
    val bonusDataCU = if (dataCUMatcher.find()) {
        BonusDataCU(
            bonusDataCuCount = dataCUMatcher.group("bonusDataCu")?.toBytes()?.toLong() ?: 0L,
            bonusDataCuDueDate = dataCUMatcher.group("bonusDataCuDueDate") ?: ""
        )
    } else BonusDataCU(bonusDataCuCount = 0L, bonusDataCuDueDate = "")

    return BonusBalance(
        credit = bonusCredit,
        data = bonusData,
        dataCu = bonusDataCU,
        unlimitedData = bonusUnlimitedData
    )
}