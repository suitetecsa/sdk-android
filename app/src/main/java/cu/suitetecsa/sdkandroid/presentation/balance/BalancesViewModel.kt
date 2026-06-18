package cu.suitetecsa.sdkandroid.presentation.balance

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.sdkandroid.data.source.PreferenceDataSource
import cu.suitetecsa.sdkandroid.domain.model.Preferences
import cu.suitetecsa.sdkandroid.domain.model.SimBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.ContactsCollector
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.balance.SdkCallback
import io.github.suitetecsa.sdk.android.balance.UssdStringCallback
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.queryBalance
import io.github.suitetecsa.sdk.android.utils.queryBonuses
import io.github.suitetecsa.sdk.android.utils.queryData
import io.github.suitetecsa.sdk.android.utils.querySms
import io.github.suitetecsa.sdk.android.utils.queryVoice
import io.github.suitetecsa.sdk.android.utils.ussdFetch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BalancesViewModel @Inject constructor(
    private val preferenceDataSource: PreferenceDataSource,
    private val simCardsCollector: SimCardCollector,
    private val contactsCollector: ContactsCollector
) : ViewModel() {
    private val preferences: StateFlow<Preferences> = preferenceDataSource.preferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Preferences("")
        )
    private val currentSimCardId: String
        get() = preferences.value.currentSimCardId
    private val simCards: List<SimCard>
        @SuppressLint("MissingPermission")
        get() = simCardsCollector.collect()
    private val currentSimCard: SimCard?
        @SuppressLint("MissingPermission", "HardwareIds")
        get() {
            return if (currentSimCardId.isNotBlank()) {
                simCards.firstOrNull {
                    it.telephony.subscriberId == preferences.value.currentSimCardId
                }.let { it ?: simCards.firstOrNull() }
            } else {
                simCards.firstOrNull()
            }
        }

    private val _state =
        mutableStateOf(BalanceState(currentSimCard = currentSimCard, simCards = simCards))
    val state: State<BalanceState>
        get() = _state
    var canUpdate = true
        private set

    @SuppressLint("MissingPermission", "NewApi", "HardwareIds")
    fun onEvent(event: BalanceEvent) {
        when (event) {
            is BalanceEvent.ChangeSimCard -> {
                viewModelScope.launch {
                    preferenceDataSource.updateCurrentSimCardId(event.simCard.telephony.subscriberId)
                    _state.value =
                        BalanceState(currentSimCard = currentSimCard, simCards = simCards)
                    fetchAll()
                }
            }

            is BalanceEvent.TurnUsageBasedPricing -> {
                if (canUpdate) {
                    canUpdate = false
                    _state.value = _state.value.copy(loading = true)
                    turnUsageBasedPricing(event.isActive)
                }
            }

            BalanceEvent.CollectContacts -> {
                _state.value = _state.value.copy(
                    contacts = contactsCollector.collect().filter {
                        it.shortNumber != null
                    }
                )
            }

            BalanceEvent.FetchAll -> {
                fetchAll()
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun fetchAll() {
        val sim = currentSimCard ?: return
        _state.value = _state.value.copy(loading = true, consultMessage = "Consultando...", error = null)

        var pending = 5
        var fetchedBalance: SimBalance? = null
        var fetchedConsumptionRate = false
        var fetchedDataBytes: Long? = null
        var fetchedDataExpires: String? = null
        var fetchedVoiceExpires: String? = null
        var fetchedSmsExpires: String? = null
        var fetchedBonusCredit: Float? = null
        var fetchedBonusCreditExpires: Date? = null
        var fetchedBonusData: Long? = null
        var fetchedBonusDataExpires: Date? = null
        var fetchedDataCu: Long? = null
        var fetchedDataCuExpires: Date? = null
        var fetchedUnlimitedExpires: Date? = null

        fun tryComplete() {
            val bal = fetchedBalance ?: return
            if (--pending > 0) return
            val complete = bal.copy(
                consumptionRate = fetchedConsumptionRate,
                data = fetchedDataBytes,
                dataExpires = fetchedDataExpires,
                voiceExpires = fetchedVoiceExpires,
                smsExpires = fetchedSmsExpires,
                bonusCredit = fetchedBonusCredit,
                bonusCreditExpires = fetchedBonusCreditExpires,
                bonusData = fetchedBonusData,
                bonusDataExpires = fetchedBonusDataExpires,
                dataCu = fetchedDataCu,
                dataCuExpires = fetchedDataCuExpires,
                bonusUnlimitedDataExpires = fetchedUnlimitedExpires,
            )
            _state.value = _state.value.copy(
                simBalance = complete,
                loading = false,
                consultMessage = null,
            )
        }

        sim.queryBalance(object : SdkCallback<io.github.suitetecsa.sdk.android.model.MainBalance> {
            override fun onSuccess(result: io.github.suitetecsa.sdk.android.model.MainBalance) {
                fetchedBalance = SimBalance(
                    balance = result.balance,
                    lockDate = result.lockDate,
                    deletionDate = result.deletionDate,
                    consumptionRate = false,
                    data = result.data,
                    dataExpires = null,
                    voice = result.voice,
                    voiceExpires = null,
                    sms = result.sms,
                    smsExpires = null,
                    dailyData = result.dailyData,
                    dailyDataExpires = null,
                )
                tryComplete()
            }

            override fun onFailure(throwable: Throwable) {
                _state.value = _state.value.copy(
                    loading = false,
                    consultMessage = null,
                    error = throwable.message,
                )
                pending = 0
            }
        })

        sim.queryData(object : SdkCallback<io.github.suitetecsa.sdk.android.model.MainData> {
            override fun onSuccess(result: io.github.suitetecsa.sdk.android.model.MainData) {
                fetchedConsumptionRate = result.consumptionRate
                fetchedDataBytes = result.data
                fetchedDataExpires = result.expires
                tryComplete()
            }

            override fun onFailure(throwable: Throwable) {
                pending--
            }
        })

        sim.queryVoice(object : SdkCallback<io.github.suitetecsa.sdk.android.balance.response.VoiceBalance> {
            override fun onSuccess(result: io.github.suitetecsa.sdk.android.balance.response.VoiceBalance) {
                fetchedVoiceExpires = result.expires
                tryComplete()
            }

            override fun onFailure(throwable: Throwable) {
                pending--
            }
        })

        sim.querySms(object : SdkCallback<io.github.suitetecsa.sdk.android.balance.response.MessagesBalance> {
            override fun onSuccess(result: io.github.suitetecsa.sdk.android.balance.response.MessagesBalance) {
                fetchedSmsExpires = result.expires
                tryComplete()
            }

            override fun onFailure(throwable: Throwable) {
                pending--
            }
        })

        sim.queryBonuses(object : SdkCallback<io.github.suitetecsa.sdk.android.balance.response.BonusBalance> {
            override fun onSuccess(result: io.github.suitetecsa.sdk.android.balance.response.BonusBalance) {
                fetchedBonusCredit = result.credit?.data
                fetchedBonusCreditExpires = result.credit?.expires
                fetchedBonusData = result.data?.data
                fetchedBonusDataExpires = result.data?.expires
                fetchedDataCu = result.dataCu?.data
                fetchedDataCuExpires = result.dataCu?.expires
                fetchedUnlimitedExpires = result.unlimitedData?.expires
                tryComplete()
            }

            override fun onFailure(throwable: Throwable) {
                pending--
            }
        })
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun turnUsageBasedPricing(isActive: Boolean) {
        val ussdCode = if (!isActive) {
            "*133*1*1*2${"#".toUri()}"
        } else {
            "*133*1*1*1${"#".toUri()}"
        }
        currentSimCard?.also {
            it.ussdFetch(
                ussdCode,
                object : UssdStringCallback {
                    override fun onSuccess(rawResponse: String) {
                        if (rawResponse == UssdResponse.PROCESSING_RESPONSE) {
                            _state.value.simBalance?.let { bal ->
                                _state.value = _state.value.copy(
                                    simBalance = bal.copy(
                                        consumptionRate = isActive
                                    )
                                )
                            } ?: run {
                                _state.value = _state.value.copy(
                                    simBalance = SimBalance(
                                        balance = 0f,
                                        lockDate = java.util.Date(),
                                        deletionDate = java.util.Date(),
                                        consumptionRate = isActive,
                                        data = null,
                                        dataExpires = null,
                                        voice = null,
                                        voiceExpires = null,
                                        sms = null,
                                        smsExpires = null,
                                        dailyData = null,
                                        dailyDataExpires = null,
                                    )
                                )
                            }
                        }
                        _state.value = _state.value.copy(
                            consultMessage = null,
                            loading = false,
                        )
                        canUpdate = true
                    }

                    override fun onFailure(throwable: Throwable) {
                        _state.value = _state.value.copy(
                            consultMessage = null,
                            loading = false,
                        )
                        canUpdate = true
                        throwable.printStackTrace()
                    }
                }
            )
        }
    }
}
