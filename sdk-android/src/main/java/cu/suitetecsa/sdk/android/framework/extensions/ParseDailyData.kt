package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.domain.model.DailyData
import cu.suitetecsa.sdk.android.framework.toBytes

/**
 * Parses the daily data from a given CharSequence and returns a DailyData object.
 *
 * @return The parsed daily data as a DailyData object, or null if the data cannot be parsed.
 */
fun CharSequence.parseDailyData(): DailyData? {
    // Regular expression to match the daily data pattern
    val dailyDataRegex =
        (
            """Diaria:\s+(?<dataDaily>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+horas)?\."""
            ).toRegex()

    // Parse the daily data
    return dailyDataRegex.find(this)?.let { matchResult ->
        DailyData(
            data = matchResult.groups["dataDaily"]?.value?.toBytes()!!,
            remainingHours = matchResult.groups["dueDate"]?.value?.toInt()
        )
    }
}
