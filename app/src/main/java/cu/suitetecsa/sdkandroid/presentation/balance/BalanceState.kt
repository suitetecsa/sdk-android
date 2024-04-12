package cu.suitetecsa.sdkandroid.presentation.balance

import io.github.suitetecsa.sdk.android.model.BonusCredit
import io.github.suitetecsa.sdk.android.model.BonusData
import io.github.suitetecsa.sdk.android.model.BonusUnlimitedData
import io.github.suitetecsa.sdk.android.model.Contact
import io.github.suitetecsa.sdk.android.model.DailyData
import io.github.suitetecsa.sdk.android.model.DataCu
import io.github.suitetecsa.sdk.android.model.MailData
import io.github.suitetecsa.sdk.android.model.MainData
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice

data class BalanceState(
    val currentSimCard: SimCard? = null,
    val simCards: List<SimCard> = listOf(),
    val balance: Float = 0f,
    val data: MainData? = null,
    val voice: Voice? = null,
    val sms: Sms? = null,
    val dailyData: DailyData? = null,
    val mailData: MailData? = null,
    val activeUntil: String? = null,
    val mainBalanceDueDate: String? = null,
    val bonusCredit: BonusCredit? = null,
    val bonusData: BonusData? = null,
    val dataCu: DataCu? = null,
    val bonusUnlimitedData: BonusUnlimitedData? = null,
    val loading: Boolean = false,
    val consultMessage: String? = null,
    val error: String? = null,
    val errorText: String? = null,
    val contacts: List<Contact> = listOf(),
)
