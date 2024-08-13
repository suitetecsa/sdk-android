package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.model.MainData
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MainDataParser {
    @RequiresApi(Build.VERSION_CODES.O)
    private fun CharSequence.extractUsageBasedPricing() =
        """Tarifa:\s+(?<consumptionRate>[^"]*?)\.""".toRegex().find(this)
            ?.let { it.groups["consumptionRate"]!!.value != "No activa" }
            ?: run { throw ParseException(this.toString(), 0) }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CharSequence.extractData() =
        (
            """Paquetes:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+no activos)?""" +
                """(\s+validos\s+(?<expires>(\d+))\s+dias)?\."""
            )
            .toRegex().find(this)?.let {
                MainData(
                    this.extractUsageBasedPricing(),
                    it.groups["data"]?.value,
                    it.groups["dataLte"]?.value,
                    it.groups["expires"]?.value
                        ?: if (it.groups["data"]?.value != null || it.groups["dataLte"]?.value != null) {
                            "no activos"
                        } else {
                            null
                        }
                )
            } ?: run { MainData(this.extractUsageBasedPricing(), null, null, null) }

    /**
     * Parses the main data from a given CharSequence and returns a MainData object.
     *
     * @return The parsed main data as a MainData object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    fun extractMainData(input: CharSequence) = input.extractData()
}
