package io.github.suitetecsa.sdk.android.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.pow

private const val SECONDS_PER_MINUTE = 60

object StringUtils {
    @JvmStatic
    fun toSeconds(time: String): Long {
        val parts = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalSeconds: Long = 0
        for (part in parts) {
            totalSeconds = totalSeconds * SECONDS_PER_MINUTE + part.toLong()
        }
        return totalSeconds
    }

    @JvmStatic
    @Throws(ParseException::class)
    fun toDateMillis(date: String?): Long {
        val calendar = Calendar.getInstance()
        toDate(date)?.let { calendar.setTime(it) }
        return calendar.getTimeInMillis()
    }

    @Throws(ParseException::class)
    fun toDate(date: String?): Date? {
        return date?.let { SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(it) }
    }

    @JvmStatic
    fun toBytes(data: String): Long {
        val count = data.replace("[GMKBT]".toRegex(), "")
        val unit = data.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[data.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray().size - 1].uppercase(Locale.getDefault())
        return (count.toDouble() * 1024.0.pow("BKMGT".indexOf(unit[0]).toDouble())).toLong()
    }
}
