package cu.suitetecsa.sdk.ussd.uitls

import java.util.Calendar
import java.util.Date

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val MILLISECONDS = 1000

fun Date.daysBetweenDates(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val currentDate = Calendar.getInstance()
    val diffInMillis = calendar.timeInMillis - currentDate.timeInMillis
    val diffInDays = diffInMillis / (HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR * MILLISECONDS)
    return diffInDays.toInt() + 1
}
