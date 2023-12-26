package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.framework.UssdResponse.MessagesBalance

/**
 * Parses the main SMS balance from a given CharSequence and returns a UssdResponse.MessagesBalance object.
 *
 * @return The parsed main SMS balance as a UssdResponse.MessagesBalance object, or null if the data cannot be parsed.
 */
fun CharSequence.parseMainSms(): MessagesBalance? {
    // Regular expression to match the SMS balance pattern
    val smsRegex =
        """Usted dispone de\s+(?<sms>(\d+))\s+SMS(\s+no activos)?(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?(\.)?"""
            .toRegex()

    // Parse the SMS balance and due date
    return smsRegex.find(this)?.let { matchResult ->
        MessagesBalance(
            matchResult.groups["sms"]?.value!!.toInt(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    }
}
