package cu.suitetecsa.sdk.android.balance.parser

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.android.balance.response.MessagesBalance
import org.jetbrains.annotations.Contract
import java.text.ParseException

object MainSmsParser {
    /**
     * Parses the main SMS balance from a given CharSequence and returns a MessagesBalance object.
     *
     * @return The parsed main SMS balance as a MessagesBalance object, or null if the data cannot be parsed.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    @Throws(ParseException::class)
    fun extractSms(input: CharSequence) =
        """Usted dispone de\s+(?<volume>(\d+))\s+SMS(\s+no activos)?(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?(\.)?"""
            .toRegex().find(input)?.let {
                MessagesBalance(it.groups["volume"]!!.value.toLong(), it.groups["dueDate"]?.value?.toInt())
            } ?: run { throw ParseException(input.toString(), 0) }
}
