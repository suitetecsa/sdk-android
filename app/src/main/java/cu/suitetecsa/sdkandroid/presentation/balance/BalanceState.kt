package cu.suitetecsa.sdkandroid.presentation.balance

import cu.suitetecsa.sdk.android.model.BonusCredit
import cu.suitetecsa.sdk.android.model.BonusData
import cu.suitetecsa.sdk.android.model.BonusDataCU
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData
import cu.suitetecsa.sdk.android.model.DailyData
import cu.suitetecsa.sdk.android.model.MailData
import cu.suitetecsa.sdk.android.model.MainData
import cu.suitetecsa.sdk.android.model.MainSms
import cu.suitetecsa.sdk.android.model.MainVoice
import cu.suitetecsa.sdk.android.model.SimCard


data class BalanceState(
    val currentSimCard: SimCard? = null,
    val simCards: List<SimCard> = listOf(),
    val balance: Float = 0f,
    val data: MainData? = null,
    val voice: MainVoice? = null,
    val sms: MainSms? = null,
    val dailyData: DailyData? = null,
    val mailData: MailData? = null,
    val activeUntil: String? = null,
    val mainBalanceDueDate: String? = null,
    val bonusCredit: BonusCredit? = null,
    val bonusData: BonusData? = null,
    val bonusDataCU: BonusDataCU? = null,
    val bonusUnlimitedData: BonusUnlimitedData? = null,
    val loading: Boolean = false,
    val consultMessage: String? = null,
    val error: String? = null,
    val errorText: String? = null,
)
