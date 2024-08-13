package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.model.MainBalance
import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice
import java.text.ParseException

object MainBalanceParser {

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CharSequence.extractData() =
        (
            """Datos:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<dataLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)\."""
            )
            .toRegex().find(this)?.let {
                MainData(
                    false,
                    it.groups["data"]?.value,
                    it.groups["dataLte"]?.value,
                    if (it.groups["data"]?.value != null || it.groups["dataLte"]?.value != null) {
                        "no activos"
                    } else {
                        null
                    }
                )
            }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CharSequence.extractVoice() =
        """Voz:\s+(?<data>(\d{1,3}:\d{2}:\d{2}))\.""".toRegex().find(this)?.let {
            Voice(it.groups["data"]!!.value, "no activos")
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun CharSequence.extractSms() =
        """SMS:\s+(?<data>(\d+))\.""".toRegex().find(this)?.let {
            Sms(it.groups["data"]!!.value, "no activos")
        }

    /**
     * Parses the main balance data from a given CharSequence and returns a MainBalance object.
     *
     * @return The parsed main balance data as a MainBalance object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(ParseException::class)
    fun extractMainBalance(input: CharSequence) = input.extractCredit()
        .let { (balance, lockDate, deletionDate) ->
            MainBalance(
                balance,
                input.extractData(),
                input.extractVoice(),
                input.extractSms(),
                null,
                null,
                lockDate,
                deletionDate
            )
        }
}
