package cu.suitetecsa.sdk.ussd.uitls

private const val SIZE_UNIT_MAX_LENGTH: Double = 1024.0

internal fun Double.toSizeString(): String {
    val sizeUnits = arrayOf("bytes", "KB", "MB", "GB", "TB")
    var sizeValue = this
    var sizeUnitIndex = 0
    while (sizeValue >= SIZE_UNIT_MAX_LENGTH && sizeUnitIndex < sizeUnits.lastIndex) {
        sizeValue /= SIZE_UNIT_MAX_LENGTH
        sizeUnitIndex++
    }
    return "%.2f %s".format(sizeValue, sizeUnits[sizeUnitIndex]).replace(".", ",")
}