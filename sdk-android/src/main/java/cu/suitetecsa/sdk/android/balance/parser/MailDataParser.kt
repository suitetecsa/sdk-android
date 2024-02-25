package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.model.MailData
import cu.suitetecsa.sdk.android.utils.StringUtils
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MailDataParser {
    /**
     * Parses the mail data from a given CharSequence and returns a MailData object.
     *
     * @return The parsed mail data as a MailData object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    @Throws(ParseException::class)
    fun parseMailData(input: CharSequence): MailData =
        ("""Mensajeria:\s+(?<volume>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\.""").toRegex().find(input)?.let {
            MailData(
                StringUtils.toBytes(it.groups["volume"]!!.value),
                it.groups["dueDate"]?.value?.toInt()
            )
        } ?: run { throw ParseException(input.toString(), 0) }
}
