package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.balance.response.VoiceBalance
import cu.suitetecsa.sdk.android.utils.StringUtils
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MainVoiceParser {
    /**
     * Parses the main voice balance from a given CharSequence and returns a VoiceBalance object.
     *
     * @return The parsed main voice balance as a VoiceBalance object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    @Throws(ParseException::class)
    fun extractVoice(input: CharSequence): VoiceBalance =
        ("""Usted dispone de\s+(?<volume>(\d+:\d{2}:\d{2}))\s+MIN(\s+no activos)?""" +
                """(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?""").toRegex().find(input)?.let {
            VoiceBalance(
                StringUtils.toSeconds(it.groups["volume"]!!.value),
                it.groups["dueDate"]?.value?.toInt()
            )
        } ?: run { throw ParseException(input.toString(), 0) }
}
