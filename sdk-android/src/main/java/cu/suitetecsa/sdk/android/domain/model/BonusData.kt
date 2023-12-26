package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the bonus data count, LTE bonus data count, and due date for bonus data in a
 * USSD balance request.
 *
 * @param bonusDataCount The count of bonus data.
 * @param bonusDataCountLte The count of LTE bonus data.
 * @param bonusDataDueDate The due date for the bonus data, or null if there is no due date.
 */
data class BonusData(
    val bonusDataCount: Double?,
    val bonusDataCountLte: Double?,
    val bonusDataDueDate: String?
)
