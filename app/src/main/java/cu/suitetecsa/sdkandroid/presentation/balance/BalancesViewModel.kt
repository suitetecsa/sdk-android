package cu.suitetecsa.sdkandroid.presentation.balance

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.sdk.android.SimCardsAPI
import cu.suitetecsa.sdk.android.domain.model.MainData
import cu.suitetecsa.sdk.android.domain.model.MainSms
import cu.suitetecsa.sdk.android.domain.model.MainVoice
import cu.suitetecsa.sdk.android.domain.model.SimCard
import cu.suitetecsa.sdk.android.framework.ConsultBalanceCallBack
import cu.suitetecsa.sdk.android.framework.UssdConsultType
import cu.suitetecsa.sdk.android.framework.UssdResponse
import cu.suitetecsa.sdk.android.framework.consultBalance
import cu.suitetecsa.sdk.android.framework.ussdExecute
import cu.suitetecsa.sdkandroid.data.source.PreferenceDataSource
import cu.suitetecsa.sdkandroid.domain.model.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalancesViewModel @Inject constructor(
    private val preferenceDataSource: PreferenceDataSource,
    private val simCardsAPI: SimCardsAPI,
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
        get() = simCardsAPI.getSimCards()
    private val currentSimCard: SimCard?
        get() {
            return if (currentSimCardId.isNotBlank()) {
                simCards.firstOrNull {
                    it.serialNumber == preferences.value.currentSimCardId
                }.let { it ?: simCards.firstOrNull() }
            } else { simCards.firstOrNull() }
        }

    private val _state = mutableStateOf(BalanceState(currentSimCard = currentSimCard, simCards = simCards))
    val state: State<BalanceState>
        get() = _state
    var canUpdate = true
        private set
    var consultType: UssdConsultType? = null
        private set

    @SuppressLint("MissingPermission", "NewApi")
    fun onEvent(event: BalanceEvent) {
        when (event) {
            is BalanceEvent.UpdateBalance -> {
                if (canUpdate) {
                    canUpdate = false
                    _state.value = _state.value.copy(loading = true)
                    currentSimCard?.also {
                        updateBalance(it)
                    }
                }
            }
            is BalanceEvent.ChangeSimCard -> {
                viewModelScope.launch {
                    preferenceDataSource.updateCurrentSimCardId(event.simCard.serialNumber)
                    _state.value = BalanceState(currentSimCard = currentSimCard, simCards = simCards)
                }
            }

            is BalanceEvent.TurnUsageBasedPricing -> {
                if (canUpdate) {
                    canUpdate = false
                    _state.value = _state.value.copy(loading = true)
                    turnUsageBasedPricing(event.isActive)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    private fun updateBalance(simCard: SimCard) {
        simCard.consultBalance(
            object : ConsultBalanceCallBack {
                override fun onRequesting(consultType: UssdConsultType) {
                    this@BalancesViewModel.consultType = consultType
                    val consultMessage = when (consultType) {
                        UssdConsultType.BonusBalance -> "Consultando Bonos"
                        UssdConsultType.DataBalance -> "Consultando Datos"
                        UssdConsultType.MessagesBalance -> "Consultando SMS"
                        UssdConsultType.PrincipalBalance -> "Consultando Saldo"
                        UssdConsultType.VoiceBalance -> "Consultando Minutos"
                        is UssdConsultType.Custom -> ""
                    }
                    _state.value = _state.value.copy(
                        consultMessage = consultMessage
                    )
                }

                override fun onSuccess(ussdResponse: UssdResponse) {
                    when (ussdResponse) {
                        is UssdResponse.BonusBalance -> {
                            _state.value = _state.value.copy(
                                bonusCredit = ussdResponse.credit,
                                bonusData = ussdResponse.data,
                                bonusDataCU = ussdResponse.dataCu,
                                bonusUnlimitedData = ussdResponse.unlimitedData,
                            )
                            _state.value = _state.value.copy(
                                consultMessage = null,
                                loading = false,
                            )
                            canUpdate = true
                        }
                        is UssdResponse.DataBalance -> {
                            _state.value = _state.value.copy(
                                data = MainData(
                                    usageBasedPricing = ussdResponse.usageBasedPricing,
                                    data = ussdResponse.data,
                                    dataLte = ussdResponse.dataLte,
                                    remainingDays = ussdResponse.remainingDays
                                ),
                                mailData = ussdResponse.mailData,
                                dailyData = ussdResponse.dailyData
                            )
                        }
                        is UssdResponse.MessagesBalance -> {
                            _state.value = _state.value.copy(
                                sms = MainSms(
                                    mainSms = ussdResponse.count,
                                    remainingDays = ussdResponse.remainingDays
                                )
                            )
                        }
                        is UssdResponse.PrincipalBalance -> {
                            _state.value = _state.value.copy(
                                balance = ussdResponse.credit,
                                activeUntil = ussdResponse.activeUntil,
                                mainBalanceDueDate = ussdResponse.dueDate,
                            )
                        }
                        is UssdResponse.VoiceBalance -> {
                            _state.value = _state.value.copy(
                                voice = MainVoice(
                                    mainVoice = ussdResponse.time,
                                    remainingDays = ussdResponse.remainingDays
                                )
                            )
                        }
                        is UssdResponse.Custom -> {}
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    throwable.printStackTrace()
                    if (consultType is UssdConsultType.BonusBalance) {
                        _state.value = _state.value.copy(
                            consultMessage = null,
                            loading = false,
                        )
                        canUpdate = true
                    }
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    private fun turnUsageBasedPricing(isActive: Boolean) {
        val ussdCode = if (!isActive) {
            "*133*1*1*2${Uri.parse("#")}"
        } else { "*133*1*1*1${Uri.parse("#")}" }
        currentSimCard?.also {
            it.ussdExecute(
                ussdCode,
                object : ConsultBalanceCallBack {
                    override fun onRequesting(consultType: UssdConsultType) {
                        _state.value = _state.value.copy(
                            consultMessage = if (!isActive) {
                                "Desactivando TPC"
                            } else { "Activando TPC" }
                        )
                    }

                    override fun onSuccess(ussdResponse: UssdResponse) {
                        when (ussdResponse) {
                            is UssdResponse.Custom -> {
                                if (ussdResponse.response == UssdResponse.PROCESSING_RESPONSE) {
                                    _state.value.data?.let { data ->
                                        _state.value = _state.value.copy(
                                            data = data.copy(usageBasedPricing = isActive)
                                        )
                                    } ?: run {
                                        _state.value = _state.value.copy(
                                            data = MainData(
                                                usageBasedPricing = isActive,
                                                data = null,
                                                dataLte = null,
                                                remainingDays = null
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
                            else -> {}
                        }
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
