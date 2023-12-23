package cu.suitetecsa.sdk.android.framework

import java.util.Calendar
import java.util.Date

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val MILLISECONDS = 1000

/**
 * Calculates the number of days between the current date and the given date.
 *
 * @return The number of days between the current date and the given date.
 */
fun Date.daysBetweenDates(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val currentDate = Calendar.getInstance()
    val diffInMillis = calendar.timeInMillis - currentDate.timeInMillis
    val diffInDays = diffInMillis / (HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR * MILLISECONDS)
    return diffInDays.toInt() + 1
}
