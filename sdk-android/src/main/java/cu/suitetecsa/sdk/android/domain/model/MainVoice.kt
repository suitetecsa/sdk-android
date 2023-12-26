package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the main voice balance and remaining days for a USSD balance request.
 *
 * @param mainVoice The main voice balance.
 * @param remainingDays The remaining days for the balance.
 */
data class MainVoice(val mainVoice: Long, val remainingDays: Int?)
