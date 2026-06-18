package io.github.suitetecsa.sdk.android.balance.parser

import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.utils.asBytes
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MainDataParser {
    private fun CharSequence.extractUsageBasedPricingStatus(): Boolean {
        val regex = """Tarifa:\s+(?<consumptionRate>[^"]*?)\.""".toRegex()
        val matchResult = regex.find(this)
        return matchResult?.groups?.get("consumptionRate")?.value?.let { it != "No activa" }
            ?: throw ParseException("Pricing information not found in: $this", 0)
    }

    private fun CharSequence.extractDataInformation(): MainData? {
        val regex = (
            """Paquetes:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """(\s+no activos)?""" +
                """(\s+validos\s+(?<expires>(\d+))\s+dias)?\."""
            )
            .toRegex()

        val matchResult = regex.find(this) ?: return null

        val data = matchResult.groups["data"]?.value
        val expires = matchResult.groups["expires"]?.value

        val expirationStatus = when {
            expires != null -> expires
            data != null -> "no activos"
            else -> null
        }
        return MainData(extractUsageBasedPricingStatus(), data?.asBytes, expirationStatus)
    }

    /**
     * Parses the main data from a given CharSequence and returns a MainData object.
     *
     * @return The parsed main data as a MainData object, or null if the data cannot be parsed.
     */
    @Suppress("RethrowCaughtException")
    @JvmStatic
    @Contract("_ -> new")
    fun extractMainData(input: CharSequence): MainData {
        return try {
            input.extractDataInformation() ?: MainData(input.extractUsageBasedPricingStatus(), null, null)
        } catch (e: ParseException) {
            throw e
        }
    }
}
