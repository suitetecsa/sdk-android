package io.github.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.suitetecsa.sdk.android.model.MailData
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
        (
            """Mensajeria:\s+(?<data>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<expires>(\d+))\s+dias)?\."""
            ).toRegex().find(input)?.let {
            MailData(it.groups["data"]!!.value, it.groups["expires"]?.value ?: "no activos")
        } ?: run { throw ParseException(input.toString(), 0) }
}
