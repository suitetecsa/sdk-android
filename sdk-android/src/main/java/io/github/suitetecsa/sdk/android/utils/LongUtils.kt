package io.github.suitetecsa.sdk.android.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60
private const val SIZE_UNIT_MAX_LENGTH = 1024.0
private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val MILLISECONDS = 1000

object LongUtils {
    @JvmStatic
    fun toDateString(date: Long?): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(date!!)
        return SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)
    }

    @JvmStatic
    fun toTimeString(time: Long): String {
        val hours = time / SECONDS_IN_HOUR
        val minutes = time % SECONDS_IN_HOUR / SECONDS_IN_MINUTE
        val seconds = time % SECONDS_IN_MINUTE
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Converts a size value in bytes to a human-readable string representation.
     *
     * @return The size value formatted as a string with the appropriate unit (bytes, KB, MB, GB, TB).
     */
    @JvmStatic
    fun toSizeString(size: Long): String {
        val sizeUnits = arrayOf("bytes", "KB", "MB", "GB", "TB")
        var sizeValue = size.toDouble()
        var sizeUnitIndex = 0
        while (sizeValue >= SIZE_UNIT_MAX_LENGTH && sizeUnitIndex < sizeUnits.size - 1) {
            sizeValue /= SIZE_UNIT_MAX_LENGTH
            sizeUnitIndex++
        }
        return String.format(Locale.getDefault(), "%.2f %s", sizeValue, sizeUnits[sizeUnitIndex])
    }

    @JvmStatic
    fun toRemainingDays(date: Long): Int {
        val calendar = Calendar.getInstance()
        val diffInMillis = date - calendar.getTimeInMillis()
        return (diffInMillis / (HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR * MILLISECONDS) + 1).toInt()
    }

    @Suppress("unused")
    val Long.asDateString: String get() = toDateString(this)

    @Suppress("unused")
    val Long.asSizeString: String get() = toSizeString(this)

    @Suppress("unused")
    val Long.asTimeString: String get() = toTimeString(this)

    @Suppress("unused")
    val Long.asRemainingDays: Int get() = toRemainingDays(this)
}
