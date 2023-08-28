package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainData(): MainData {
    val usageBasedPricingRegex = """Tarifa:\s+(?<tfc>[^"]*?)\.""".toRegex()
    val usageBasedPricing = usageBasedPricingRegex.find(this.message)?.let { matchResult ->
        matchResult.groups["tfc"]?.value != "No activa"
    } ?: false

    val mainDataRegex = ("""Paquetes:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?""" +
    """(\s+\+\s+)?((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?""" +
            """(\s+no activos)?(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\.""").toRegex()
    val (data, dataLte, remainingDays) = mainDataRegex.find(this.message)?.let { matchResult ->
        Triple(
            matchResult.groups["dataAllNetwork"]?.value?.toBytes(),
            matchResult.groups["dataLte"]?.value?.toBytes(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Triple(null, null, null)

    return MainData(
            usageBasedPricing = usageBasedPricing,
            data = data,
            dataLte = dataLte,
            remainingDays = remainingDays
        )
}
