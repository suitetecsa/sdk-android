package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the mail data balance and remaining days for a USSD balance request.
 *
 * @param data The mail data balance.
 * @param remainingDays The remaining days for the balance.
 */
data class MailData(val data: Double, val remainingDays: Int?)
