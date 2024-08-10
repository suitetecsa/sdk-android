@file:Suppress("unused")

package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.model.DailyData
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
            """Diaria:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<expires>(\d+))\s+horas)?\."""
            ).toRegex().find(input)?.let {
            DailyData(it.groups["data"]!!.value, it.groups["expires"]?.value ?: "no activos")
        } ?: run { throw ParseException(input.toString(), 0) }
}

val CharSequence.asDailyData: DailyData
    @RequiresApi(Build.VERSION_CODES.O)
    get() = DailyDataParser.parseDailyData(this)
