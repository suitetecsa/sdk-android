package cu.suitetecsa.sdk.android.framework

import cu.suitetecsa.sdk.android.domain.model.BonusCredit
import cu.suitetecsa.sdk.android.domain.model.BonusData
import cu.suitetecsa.sdk.android.domain.model.BonusDataCU
import cu.suitetecsa.sdk.android.domain.model.BonusUnlimitedData
import cu.suitetecsa.sdk.android.domain.model.DailyData
import cu.suitetecsa.sdk.android.domain.model.MailData

/**
 * Sealed class representing different types of USSD responses.
 */
sealed class UssdResponse {
    /**
     * Represents the principal balance response.
     *
     * @param credit The credit amount.
     * @param activeUntil The active until date.
     * @param dueDate The due date.
     * @param consults The list of USSD consult types.
     */
    data class PrincipalBalance(
        val credit: Float,
        val activeUntil: String,
        val dueDate: String,
        val consults: List<UssdConsultType>
    ) : UssdResponse()

    /**
     * Represents the data balance response.
     *
     * @param usageBasedPricing Indicates if the pricing is usage-based.
     * @param data The data amount.
     * @param dataLte The LTE data amount.
     * @param remainingDays The remaining days.
     * @param dailyData The daily data information.
     * @param mailData The mail data information.
     */
    data class DataBalance(
        val usageBasedPricing: Boolean,
        val data: Double?,
        val dataLte: Double?,
        val remainingDays: Int?,
        val dailyData: DailyData?,
        val mailData: MailData?
    ) : UssdResponse()

    /**
     * Represents the voice balance response.
     *
     * @param time The voice time in seconds.
     * @param remainingDays The remaining days.
     */
    data class VoiceBalance(val time: Long, val remainingDays: Int?) : UssdResponse()

    /**
     * Represents the messages balance response.
     *
     * @param count The message count.
     * @param remainingDays The remaining days.
     */
    data class MessagesBalance(val count: Int, val remainingDays: Int?) : UssdResponse()

    /**
     * Represents the bonus balance response.
     *
     * @param credit The bonus credit information.
     * @param unlimitedData The bonus unlimited data information.
     * @param data The bonus data information.
     * @param dataCu The bonus data CU information.
     */
    data class BonusBalance(
        val credit: BonusCredit?,
        val unlimitedData: BonusUnlimitedData?,
        val data: BonusData?,
        val dataCu: BonusDataCU?
    ) : UssdResponse()

    data class Custom(val response: String) : UssdResponse()

    companion object {
        const val PROCESSING_RESPONSE = "Su solicitud esta siendo procesada."
    }
}
