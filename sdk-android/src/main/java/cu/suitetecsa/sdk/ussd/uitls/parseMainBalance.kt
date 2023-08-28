package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainBalance
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainBalance(): MainBalance {
    val dataRegex =
        ("""Datos:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?\.""")
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
        ("""Saldo:\s+(?<principalCredit>([\d.]+))\s+CUP\.\s+([^"]*?)?""" +
                """Linea activa hasta\s+(?<activeUntil>(\d{2}-\d{2}-\d{2}))""" +
                """\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\.""")
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