package cu.suitetecsa.sdk.android.framework

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

private const val SIZE_UNIT_MAX_LENGTH: Double = 1024.0
private const val GB_POWER: Double = 3.0

private const val SECONDS_IN_MINUTES = 60

/**
 * Converts a string representation of a size value to bytes.
 *
 * @return The size value converted to bytes.
 * @throws IllegalArgumentException if the size unit is not valid.
 */
internal fun String.toBytes(): Double {
    val sizeUnit = this.split(" ").last()
    val sizeValue = this.replace(" $sizeUnit", "").replace(" ", "")
    return sizeValue.replace(",", ".").toDouble() * when (sizeUnit.uppercase(Locale.getDefault())) {
        "KB" -> SIZE_UNIT_MAX_LENGTH
        "MB" -> SIZE_UNIT_MAX_LENGTH.pow(2)
        "GB" -> SIZE_UNIT_MAX_LENGTH.pow(GB_POWER)
        else -> throw IllegalArgumentException("La unidad de tamaño no es válida")
    }
}

/**
 * Converts a string representation of a date to a Date object.
 *
 * @return The Date object representing the parsed date, or null if parsing fails.
 */
internal fun String.toDate(): Date? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)

/**
 * Converts a string representation of a Cubacel date to a Date object.
 *
 * @return The Date object representing the parsed Cubacel date, or null if parsing fails.
 */
internal fun String.toCubacelDate(): Date? = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(this)

/**
 * Converts a string representation of a time to seconds.
 *
 * @return The time value converted to seconds.
 */
internal fun String.toSeconds() = this.split(":").fold(0L) { acc, s -> acc * SECONDS_IN_MINUTES + s.toLong() }
