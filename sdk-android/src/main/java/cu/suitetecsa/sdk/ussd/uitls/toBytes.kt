package cu.suitetecsa.sdk.ussd.uitls

import java.util.Locale
import kotlin.math.pow

private const val SIZE_UNIT_MAX_LENGTH: Double = 1024.0

internal fun String.toBytes(): Double {
    val sizeUnit = this.split(" ").last()
    val sizeValue = this.replace(" $sizeUnit", "").replace(" ", "")
    return sizeValue.replace(",", ".").toDouble() * when (sizeUnit.uppercase(Locale.getDefault())) {
        "KB" -> SIZE_UNIT_MAX_LENGTH
        "MB" -> SIZE_UNIT_MAX_LENGTH.pow(2)
        "GB" -> SIZE_UNIT_MAX_LENGTH.pow(3)
        else -> throw IllegalArgumentException("La unidad de tamaño no es válida")
    }
}
