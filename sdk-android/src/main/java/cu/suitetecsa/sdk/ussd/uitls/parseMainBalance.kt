package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainBalance
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainBalance(): MainBalance {
    val creditPattern =
        Pattern.compile("""Saldo:\s+(?<principalCredit>([\d.]+))\s+CUP\.\s+([^"]*?)?Linea activa hasta\s+(?<activeUntil>(\d{2}-\d{2}-\d{2}))\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\.""")
    val dataPattern =
        Pattern.compile("""Datos:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?\.""")
    val voicePattern = Pattern.compile("""Voz:\s+(?<voice>(\d{1,3}:\d{2}:\d{2}))\.""")
    val smsPattern = Pattern.compile("""SMS:\s+(?<sms>(\d+))\.""")

    val dataMatcher = dataPattern.matcher(this.message)
    val mainData = if (dataMatcher.find()) {
        MainData(
            usageBasedPricing = false,
            mainData = dataMatcher.group("dataAllNetwork")?.toBytes()?.toLong() ?: 0L,
            mainDataLte = dataMatcher.group("dataLte")?.toBytes()?.toLong() ?: 0L,
            mainDataDueDate = ""
        )
    } else MainData(
        usageBasedPricing = false,
        mainData = 0L,
        mainDataLte = 0L,
        mainDataDueDate = ""
    )

    val voiceMatcher = voicePattern.matcher(this.message)
    val mainVoice = if (voiceMatcher.find()) {
        MainVoice(
            mainVoice = voiceMatcher.group("voice")?.toSeconds() ?: 0L,
            mainVoiceDueDate = ""
        )
    } else MainVoice(0L, "")

    val smsMatcher = smsPattern.matcher(this.message)
    val mainSms = if (smsMatcher.find()) {
        MainSms(mainSms = smsMatcher.group("sms")?.toInt() ?: 0, mainSmsDueDate = "")
    } else MainSms(mainSms = 0, mainSmsDueDate = "")

    val creditMatcher = creditPattern.matcher(this.message)
    return if (creditMatcher.find()) {
        MainBalance(
            credit = creditMatcher.group("principalCredit")?.toFloatOrNull() ?: 0f,
            activeUntil = creditMatcher.group("activeUntil") ?: "",
            mainBalanceDueDate = creditMatcher.group("dueDate") ?: "",
            data = mainData,
            voice = mainVoice,
            sms = mainSms
        )
    } else MainBalance(
        credit = 0f,
        activeUntil = "",
        mainBalanceDueDate = "",
        data = mainData,
        voice = mainVoice,
        sms = mainSms
    )
}