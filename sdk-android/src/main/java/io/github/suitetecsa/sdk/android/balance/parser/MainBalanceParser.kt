package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.model.MainBalance
import io.github.suitetecsa.sdk.android.utils.asBytes
import io.github.suitetecsa.sdk.android.utils.asDate
import io.github.suitetecsa.sdk.android.utils.asSeconds
import java.text.ParseException

object MainBalanceParser {

    private fun CharSequence.extractCredit() =
        (
            """Saldo:\s+(?<balance>([\d.]+))\s+CUP\.\s+([^"]*?)Linea activa hasta\s+""" +
                """(?<lockDate>(\d{2}-\d{2}-\d{2}))\s+vence\s+(?<deletionDate>(\d{2}-\d{2}-\d{2}))\."""
            )
            .toRegex().find(this)?.let {
                Triple(
                    it.groups["balance"]!!.value,
                    it.groups["lockDate"]!!.value,
                    it.groups["deletionDate"]!!.value
                )
            } ?: run { throw ParseException(this.toString(), 0) }

    private fun CharSequence.extractData() =
        (
            """Datos:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?\."""
            )
            .toRegex().find(this)?.let {
                it.groups["data"]?.value
            }

    private fun CharSequence.extractVoice() =
        """Voz:\s+(?<data>(\d{1,3}:\d{2}:\d{2}))\.""".toRegex().find(this)?.let {
            it.groups["data"]?.value
        }

    private fun CharSequence.extractSms() =
        """SMS:\s+(?<data>(\d+))\.""".toRegex().find(this)?.let {
            it.groups["data"]?.value
        }

    /**
     * Parses the main balance data from a given CharSequence and returns a MainBalance object.
     *
     * @return The parsed main balance data as a MainBalance object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @Throws(ParseException::class)
    fun extractMainBalance(input: CharSequence) = input.extractCredit()
        .let { (balance, lockDate, deletionDate) ->
            MainBalance(
                balance.toFloat(),
                input.extractData()?.asBytes,
                input.extractVoice()?.asSeconds,
                input.extractSms()?.toIntOrNull(),
                null,
                lockDate.asDate!!,
                deletionDate.asDate!!
            )
        }
}
