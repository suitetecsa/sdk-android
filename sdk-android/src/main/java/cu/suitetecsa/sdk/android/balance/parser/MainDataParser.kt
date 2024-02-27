package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.model.MainData
import cu.suitetecsa.sdk.android.utils.LongUtils.toRemainingDays
import cu.suitetecsa.sdk.android.utils.StringUtils.toBytes
import cu.suitetecsa.sdk.android.utils.StringUtils.toDateMillis
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MainDataParser {
    private fun CharSequence.extractUsageBasedPricing() =
        """Tarifa:\s+(?<tfc>[^"]*?)\.""".toRegex().find(this)?.let {
            it.groups["tfc"]!!.value != "No activa"
        } ?: run { throw ParseException(this.toString(), 0) }

    private fun CharSequence.extractData() =
        ("""Paquetes:\s+(?<volume>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
            """((?<volumeLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\.""")
            .toRegex().find(this)?.let {
                MainData(
                    this.extractUsageBasedPricing(),
                    it.groups["volume"]?.value?.let { v -> toBytes(v) },
                    it.groups["volumeLte"]?.value?.let { vl -> toBytes(vl) },
                    it.groups["dueDate"]?.value?.toInt()
                )
            } ?: run { throw ParseException(this.toString(), 0) }

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
