package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.domain.model.MainData
import cu.suitetecsa.sdk.android.framework.toBytes

/**
 * Parses the main data from a given CharSequence and returns a MainData object.
 *
 * @return The parsed main data as a MainData object, or null if the data cannot be parsed.
 */
fun CharSequence.parseMainData(): MainData? {
    // Regular expression to match the usageBasedPricing pattern
    val usageBasedPricingRegex = """Tarifa:\s+(?<tfc>[^"]*?)\.""".toRegex()
    // Parse the usageBasedPricing
    val usageBasedPricing = usageBasedPricingRegex.find(this)?.let { matchResult ->
        matchResult.groups["tfc"]?.value != "No activa"
    } ?: run { return null }

    // Regular expression to match the dataAllNetwork, dataLte, and remainingDays pattern
    val mainDataRegex = (
        """Paquetes:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?""" +
            """(\s+\+\s+)?((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?""" +
            """(\s+no activos)?(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\."""
        ).toRegex()
    // Parse the dataAllNetwork, dataLte, and remainingDays
    val (data, dataLte, remainingDays) = mainDataRegex.find(this)?.let { matchResult ->
        Triple(
            matchResult.groups["dataAllNetwork"]?.value?.toBytes(),
            matchResult.groups["dataLte"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Triple(null, null, null)

    // Create and return the MainData object
    return MainData(
        usageBasedPricing = usageBasedPricing,
        data = data,
        dataLte = dataLte,
        remainingDays = remainingDays
    )
}
