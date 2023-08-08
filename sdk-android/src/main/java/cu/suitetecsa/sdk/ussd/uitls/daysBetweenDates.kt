package cu.suitetecsa.sdk.ussd.uitls

import java.util.Calendar
import java.util.Date

fun Date.daysBetweenDates(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val currentDate = Calendar.getInstance()
    val diffInMillis = calendar.timeInMillis - currentDate.timeInMillis
    val difInDays = diffInMillis / (24 * 60 * 60 * 1000)
    return difInDays.toInt() + 1
}