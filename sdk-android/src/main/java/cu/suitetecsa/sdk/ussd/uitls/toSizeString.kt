package cu.suitetecsa.sdk.ussd.uitls

internal fun Long.toSizeString(): String {
    val sizeUnits = arrayOf("bytes", "KB", "MB", "GB", "TB")
    var sizeValue = this.toDouble()
    var sizeUnitIndex = 0
    while (sizeValue >= 1024 && sizeUnitIndex < sizeUnits.lastIndex) {
        sizeValue /= 1024
        sizeUnitIndex++
    }
    return "%.2f %s".format(sizeValue, sizeUnits[sizeUnitIndex]).replace(".", ",")
}