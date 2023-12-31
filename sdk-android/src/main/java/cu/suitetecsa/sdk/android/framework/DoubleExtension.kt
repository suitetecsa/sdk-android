package cu.suitetecsa.sdk.android.framework

private const val SIZE_UNIT_MAX_LENGTH: Double = 1024.0

/**
 * Converts a size value in bytes to a human-readable string representation.
 *
 * @return The size value formatted as a string with the appropriate unit (bytes, KB, MB, GB, TB).
 */
fun Double.toSizeString(): String {
    val sizeUnits = arrayOf("bytes", "KB", "MB", "GB", "TB")
    var sizeValue = this
    var sizeUnitIndex = 0
    while (sizeValue >= SIZE_UNIT_MAX_LENGTH && sizeUnitIndex < sizeUnits.lastIndex) {
        sizeValue /= SIZE_UNIT_MAX_LENGTH
        sizeUnitIndex++
    }
    return "%.2f %s".format(sizeValue, sizeUnits[sizeUnitIndex]).replace(".", ",")
}
