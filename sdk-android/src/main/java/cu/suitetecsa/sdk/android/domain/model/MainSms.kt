package cu.suitetecsa.sdk.android.domain.model

/**
 * Data class representing the main SMS balance and remaining days for a USSD balance request.
 *
 * @param mainSms The main SMS balance.
 * @param remainingDays The remaining days for the balance.
 */
data class MainSms(val mainSms: Int, val remainingDays: Int?)
