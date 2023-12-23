package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the main balance information for a USSD balance request.
 *
 * @param credit The credit balance.
 * @param data The main data balance.
 * @param voice The main voice balance.
 * @param sms The main SMS balance.
 * @param dailyData The daily data balance.
 * @param mailData The mail data balance.
 * @param activeUntil The active until date.
 * @param mainBalanceDueDate The main balance due date.
 */
internal data class MainBalance(
    val credit: Float,
    var data: MainData?,
    var voice: MainVoice?,
    var sms: MainSms?,
    var dailyData: DailyData?,
    var mailData: MailData?,
    val activeUntil: String,
    val mainBalanceDueDate: String
)
