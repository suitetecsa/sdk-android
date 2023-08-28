package cu.suitetecsa.sdk.ussd.uitls

import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.UssdResponse

fun UssdResponse.parseDailyData(): DailyData {
    val dailyDataRegex =
        ("""Diaria:\s+(?<dataDaily>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+horas)?\.""").toRegex()

    val (data, remainingHours) = dailyDataRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["dataDaily"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return DailyData(data = data, remainingHours = remainingHours)
}