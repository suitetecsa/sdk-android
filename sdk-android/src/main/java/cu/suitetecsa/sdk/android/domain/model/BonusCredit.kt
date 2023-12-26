package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the bonus credit amount and due date for bonus credit in a USSD balance request.
 *
 * @param credit The bonus credit amount.
 * @param bonusCreditDueDate The due date for the bonus credit, or null if there is no due date.
 */
data class BonusCredit(
    val credit: Float,
    val bonusCreditDueDate: String
)
