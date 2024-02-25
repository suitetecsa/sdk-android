package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.model.MainBalance
import cu.suitetecsa.sdk.android.model.MainData
import cu.suitetecsa.sdk.android.model.Sms
import cu.suitetecsa.sdk.android.model.Voice
import cu.suitetecsa.sdk.android.utils.StringUtils.toBytes
import cu.suitetecsa.sdk.android.utils.StringUtils.toDateMillis
import cu.suitetecsa.sdk.android.utils.StringUtils.toSeconds
import java.text.ParseException

object MainBalanceParser {
    private fun CharSequence.extractCredit() =
        ("""Saldo:\s+(?<volume>([\d.]+))\s+CUP\.\s+([^"]*?)Linea activa hasta\s+""" +
            """(?<activeUntil>(\d{2}-\d{2}-\d{2}))\s+vence\s+(?<dueDate>(\d{2}-\d{2}-\d{2}))\.""")
            .toRegex().find(this)?.let {
                Triple(
                    it.groups["volume"]!!.value.toDouble(),
                    toDateMillis(it.groups["activeUntil"]!!.value),
                    toDateMillis(it.groups["dueDate"]!!.value)
                )
            } ?: run { throw ParseException(this.toString(), 0) }

    private fun CharSequence.extractData() =
        ("""Datos:\s+(?<volume>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+\+\s+)?""" +
                """((?<volumeLte>(\d+(\.\d+)?)(\s)*([GMK])?B)\s+LTE)\.""")
            .toRegex().find(this)?.let {
                MainData(
                    false,
                    it.groups["volume"]?.value?.let { v -> toBytes(v) },
                    it.groups["volumeLte"]?.value?.let { vl -> toBytes(vl) },
                    null
                )
            }

    private fun CharSequence.extractVoice() =
        """Voz:\s+(?<volume>(\d{1,3}:\d{2}:\d{2}))\.""".toRegex().find(this)?.let {
            Voice(toSeconds(it.groups["volume"]!!.value), null)
        }

    private fun CharSequence.extractSms() =
        """SMS:\s+(?<volume>(\d+))\.""".toRegex().find(this)?.let {
            Sms(it.groups["volume"]!!.value.toLong(), null)
        }

    /**
     * Parses the main balance data from a given CharSequence and returns a MainBalance object.
     *
     * @return The parsed main balance data as a MainBalance object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(ParseException::class)
    fun extractMainBalance(input: CharSequence) = input.extractCredit().let { (credit, activeUntil, dueDate) ->
        MainBalance(
            credit,
            input.extractData(),
            input.extractVoice(),
            input.extractSms(),
            null,
            null,
            activeUntil,
            dueDate
        )
    }
}
