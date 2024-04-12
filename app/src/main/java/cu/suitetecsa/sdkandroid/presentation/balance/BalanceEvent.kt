package cu.suitetecsa.sdkandroid.presentation.balance

import io.github.suitetecsa.sdk.android.model.SimCard

sealed class BalanceEvent {
    data object UpdateBalance : BalanceEvent()
    data class ChangeSimCard(val simCard: SimCard) : BalanceEvent()
    data class TurnUsageBasedPricing(val isActive: Boolean) : BalanceEvent()
    data object CollectContacts : BalanceEvent()
}
