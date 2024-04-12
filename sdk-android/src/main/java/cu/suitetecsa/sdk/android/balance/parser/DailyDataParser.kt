@file:Suppress("unused")

package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.model.DailyData
import cu.suitetecsa.sdk.android.utils.StringUtils
import org.jetbrains.annotations.Contract
import java.text.ParseException

object DailyDataParser {
    /**
     * Parses the daily data from a given CharSequence and returns a DailyData object.
     *
     * @return The parsed daily data as a DailyData object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    @Throws(ParseException::class)
    fun parseDailyData(input: CharSequence): DailyData =
        (
            """Diaria:\s+(?<volume>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+horas)?\."""
            ).toRegex().find(input)?.let {
            DailyData(
                StringUtils.toBytes(it.groups["volume"]!!.value),
                it.groups["dueDate"]?.value?.toInt()
            )
        } ?: run { throw ParseException(input.toString(), 0) }
}

val CharSequence.asDailyData: DailyData
    @RequiresApi(Build.VERSION_CODES.O)
    get() = DailyDataParser.parseDailyData(this)
