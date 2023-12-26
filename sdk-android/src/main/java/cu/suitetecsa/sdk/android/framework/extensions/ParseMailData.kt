package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.domain.model.MailData
import cu.suitetecsa.sdk.android.framework.toBytes

/**
 * Parses the mail data from a given CharSequence and returns a MailData object.
 *
 * @return The parsed mail data as a MailData object, or null if the data cannot be parsed.
 */
fun CharSequence.parseMailData(): MailData? {
    // Regular expression to match the mail data pattern
    val mailDataRegex =
        (
            """Mensajeria:\s+(?<dataMail>(\d+(\.\d+)?)(\s)*([GMK])?B)?(\s+no activos)?""" +
                """(\s+validos\s+(?<dueDate>(\d+))\s+dias)?\."""
            )
            .toRegex()

    // Parse the mail data
    return mailDataRegex.find(this)?.let { matchResult ->
        MailData(
            data = matchResult.groups["dataMail"]?.value?.toBytes()!!,
            remainingDays = matchResult.groups["dueDate"]?.value?.toInt()
        )
    }
}
