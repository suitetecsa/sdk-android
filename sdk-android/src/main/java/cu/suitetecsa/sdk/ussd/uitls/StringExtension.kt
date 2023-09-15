package cu.suitetecsa.sdk.ussd.uitls

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

private const val SIZE_UNIT_MAX_LENGTH: Double = 1024.0
private const val GB_POWER: Double = 3.0

private const val SECONDS_IN_MINUTES = 60

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

internal fun String.toDate(): Date? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)
internal fun String.toCubacelDate(): Date? = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(this)

internal fun String.toSeconds() = this.split(":").fold(0L) { acc, s -> acc * SECONDS_IN_MINUTES + s.toLong() }
