package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.domain.model.MainBalance
import cu.suitetecsa.sdk.android.domain.model.MainData
import cu.suitetecsa.sdk.android.domain.model.MainSms
import cu.suitetecsa.sdk.android.domain.model.MainVoice
import cu.suitetecsa.sdk.android.framework.toBytes
import cu.suitetecsa.sdk.android.framework.toSeconds

/**
 * Parses the main balance data from a given CharSequence and returns a MainBalance object.
 *
 * @return The parsed main balance data as a MainBalance object, or null if the data cannot be parsed.
 */
internal fun CharSequence.parseMainBalance(): MainBalance? {
    // Regular expression to match the credit, activeUntil, and dueDate pattern
    val creditRegex =
        (
            """Saldo:\s+(?<principalCredit>([\d.]+))\s+CUP\.\s+([^"]*?)?""" +
                """Linea activa hasta\s+(?<activeUntil>(\d{2}-\d{2}-\d{2}))""" +
                """\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\."""
            )
            .toRegex()
    // Parse the credit, activeUntil, and dueDate
    val (credit, activeUntil, dueDate) = creditRegex.find(this)?.let { matchResult ->
        Triple(
            matchResult.groups["principalCredit"]?.value!!.toFloat(),
            matchResult.groups["activeUntil"]?.value!!,
            matchResult.groups["dueDate"]?.value!!
        )
    } ?: run { return null }

    // Regular expression to match the dataAllNetwork and dataLte pattern
    val dataRegex =
        (
            """Datos:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?\."""
            )
            .toRegex()

    // Parse the dataAllNetwork and dataLte
    val (dataAllNetwork, dataLte) = dataRegex.find(this)?.let { matchResult ->
        Pair(
            matchResult.groups["dataAllNetwork"]?.value?.toBytes(),
            matchResult.groups["dataLte"]?.value?.toBytes()
        )
    } ?: Pair(null, null)

    // Create the MainData object
    val mainData = MainData(
        usageBasedPricing = false,
        data = dataAllNetwork,
        dataLte = dataLte,
        remainingDays = null
    )

    // Regular expression to match the voice pattern
    val voiceRegex = """Voz:\s+(?<voice>(\d{1,3}:\d{2}:\d{2}))\.""".toRegex()
    // Parse the voice
    val voice = voiceRegex.find(this)?.groups?.get("voice")?.value?.toSeconds()
    // Create the MainVoice object
    val mainVoice = voice?.let { MainVoice(mainVoice = it, remainingDays = null) }

    // Regular expression to match the sms pattern
    val smsRegex = """SMS:\s+(?<sms>(\d+))\.""".toRegex()
    // Parse the sms
    val sms = smsRegex.find(this)?.groups?.get("sms")?.value?.toInt()
    // Create the MainSms object
    val mainSms = sms?.let { MainSms(mainSms = it, remainingDays = 0) }

    // Create and return the MainBalance object
    return MainBalance(
        credit = credit,
        activeUntil = activeUntil,
        mainBalanceDueDate = dueDate,
        data = mainData,
        voice = mainVoice,
        sms = mainSms,
        dailyData = null,
        mailData = null
    )
}
