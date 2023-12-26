package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the bonus data count and due date for bonus data in a USSD balance request.
 *
 * @param bonusDataCuCount The count of bonus data.
 * @param bonusDataCuDueDate The due date for the bonus data, or null if there is no due date.
 */
data class BonusDataCU(
    val bonusDataCuCount: Double,
    val bonusDataCuDueDate: String?
)
