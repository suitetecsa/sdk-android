package cu.suitetecsa.sdkandroid.presentation.balance

import cu.suitetecsa.sdkandroid.domain.model.SimBalance
import io.github.suitetecsa.sdk.android.model.BonusCredit
import io.github.suitetecsa.sdk.android.model.BonusData
import io.github.suitetecsa.sdk.android.model.BonusUnlimitedData
import io.github.suitetecsa.sdk.android.model.Contact
import io.github.suitetecsa.sdk.android.model.DataCu
import io.github.suitetecsa.sdk.android.model.SimCard

data class BalanceState(
    val currentSimCard: SimCard? = null,
    val simCards: List<SimCard> = listOf(),
    val simBalance: SimBalance? = null,
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
