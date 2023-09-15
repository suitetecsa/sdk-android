package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData
import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainBalance
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseBonusBalance(): BonusBalance {
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

    val bonusCredit = creditRegex.find(this.message)?.let { matchResult ->
        BonusCredit(
            credit = matchResult.groups["bonusCredit"]?.value?.toFloatOrNull(),
            bonusCreditDueDate = matchResult.groups["bonusCreditDueDate"]?.value
        )
    } ?: BonusCredit(credit = null, bonusCreditDueDate = null)

    val (bonusData, bonusUnlimitedData) = dataRegex.find(this.message)?.let { dataMatcher ->
        Pair(
            BonusData(
                bonusDataCount = dataMatcher.groups["dataAllNetworkBonus"]?.value?.toBytes(),
                bonusDataCountLte = dataMatcher.groups["bonusDataLte"]?.value?.toBytes(),
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
            bonusDataCuCount = dataCUMatcher.groups["bonusDataCu"]?.value?.toBytes(),
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

fun UssdResponse.parseDailyData(): DailyData {
    val dailyDataRegex =
        (
            """Diaria:\s+(?<dataDaily>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+horas)?\."""
            ).toRegex()

    val (data, remainingHours) = dailyDataRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["dataDaily"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return DailyData(data = data, remainingHours = remainingHours)
}

fun UssdResponse.parseMailData(): MailData {
    val mailDataRegex =
        (
            """Mensajeria:\s+(?<dataMail>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\."""
            )
            .toRegex()
    val (data, remainingDays) = mailDataRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["dataMail"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MailData(data = data, remainingDays = remainingDays)
}

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainBalance(): MainBalance {
    val dataRegex =
        (
            """Datos:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?\."""
            )
            .toRegex()
    val (dataAllNetwork, dataLte) = dataRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["dataAllNetwork"]?.value?.toBytes(),
            matchResult.groups["dataLte"]?.value?.toBytes()
        )
    } ?: Pair(null, null)
    val mainData = MainData(
        usageBasedPricing = false,
        data = dataAllNetwork,
        dataLte = dataLte,
        remainingDays = null
    )

    val voiceRegex = """Voz:\s+(?<voice>(\d{1,3}:\d{2}:\d{2}))\.""".toRegex()
    val voice = voiceRegex.find(this.message)?.groups?.get("voice")?.value?.toSeconds()
    val mainVoice = MainVoice(mainVoice = voice, remainingDays = null)

    val smsRegex = """SMS:\s+(?<sms>(\d+))\.""".toRegex()
    val sms = smsRegex.find(this.message)?.groups?.get("sms")?.value?.toInt()
    val mainSms = MainSms(mainSms = sms, remainingDays = null)

    val creditRegex =
        (
            """Saldo:\s+(?<principalCredit>([\d.]+))\s+CUP\.\s+([^"]*?)?""" +
                """Linea activa hasta\s+(?<activeUntil>(\d{2}-\d{2}-\d{2}))""" +
                """\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
            )
            .toRegex()
    val (credit, activeUntil, dueDate) = creditRegex.find(this.message)?.let { matchResult ->
        Triple(
            matchResult.groups["principalCredit"]?.value?.toFloatOrNull(),
            matchResult.groups["activeUntil"]?.value,
            matchResult.groups["dueDate"]?.value
        )
    } ?: Triple(null, null, null)
    return MainBalance(
        credit = credit,
        activeUntil = activeUntil,
        mainBalanceDueDate = dueDate,
        data = mainData,
        voice = mainVoice,
        sms = mainSms,
        dailyData = DailyData(null, null),
        mailData = MailData(null, null)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainData(): MainData {
    val usageBasedPricingRegex = """Tarifa:\s+(?<tfc>[^"]*?)\.""".toRegex()
    val usageBasedPricing = usageBasedPricingRegex.find(this.message)?.let { matchResult ->
        matchResult.groups["tfc"]?.value != "No activa"
    } ?: false

    val mainDataRegex = (
        """Paquetes:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?""" +
            """(\s+\+\s+)?((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?""" +
            """(\s+no activos)?(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\."""
        ).toRegex()
    val (data, dataLte, remainingDays) = mainDataRegex.find(this.message)?.let { matchResult ->
        Triple(
            matchResult.groups["dataAllNetwork"]?.value?.toBytes(),
            matchResult.groups["dataLte"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Triple(null, null, null)

    return MainData(
        usageBasedPricing = usageBasedPricing,
        data = data,
        dataLte = dataLte,
        remainingDays = remainingDays
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainSms(): MainSms {
    val smsRegex =
        """Usted dispone de\s+(?<sms>(\d+))\s+SMS(\s+no activos)?(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?(\.)?"""
            .toRegex()

    val (sms, remainingDays) = smsRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["sms"]?.value?.toInt(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MainSms(mainSms = sms, remainingDays = remainingDays)
}

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainVoice(): MainVoice {
    val voicePattern =
        (
            """Usted dispone de\s+(?<voice>(\d+:\d{2}:\d{2}))\s+MIN(\s+no activos)?""" +
                """(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?"""
            )
            .toRegex()

    val (voice, remainingDays) = voicePattern.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["voice"]?.value?.toSeconds(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MainVoice(
        mainVoice = voice,
        remainingDays = remainingDays
    )
}
