package cu.suitetecsa.sdk.ussd.uitls

import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.UssdResponse

fun UssdResponse.parseMailData(): MailData {
    val mailDataRegex =
        ("""Mensajeria:\s+(?<dataMail>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\.""")
            .toRegex()
    val (data, remainingDays) = mailDataRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["dataMail"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MailData(data = data, remainingDays = remainingDays)
}