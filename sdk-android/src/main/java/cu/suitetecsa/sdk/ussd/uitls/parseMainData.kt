package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainData(): MainData {
    val dataPattern =
        Pattern.compile("""(Tarifa:\s+(?<tfc>[^"]*?)\.)?(\s+)?(Paquetes:\s+(?<dataAllNetwork>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+validos\s+(?<dueDate>(\d+\s+dias)))?\.)?""")
    val matcher = dataPattern.matcher(this.message)
    return if (matcher.find()) {
        MainData(
            usageBasedPricing = matcher.group("tfc")?.let {
                it != "No Activa"
            } ?: false,
            mainData = matcher.group("dataAllNetwork")?.toBytes()?.toLong() ?: 0L,
            mainDataLte = matcher.group("dataLte")?.toBytes()?.toLong() ?: 0L,
            mainDataDueDate = matcher.group("dueDate") ?: ""
        )
    } else MainData(
        usageBasedPricing = false,
        mainData = 0L,
        mainDataLte = 0L,
        mainDataDueDate = ""
    )
}