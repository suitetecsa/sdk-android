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
import cu.suitetecsa.sdk.android.SimCardCollector
import cu.suitetecsa.sdk.android.balance.ConsultBalanceCallBack
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.BONUS_BALANCE
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.CUSTOM
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.DATA_BALANCE
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.MESSAGES_BALANCE
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.PRINCIPAL_BALANCE
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest.VOICE_BALANCE
import cu.suitetecsa.sdk.android.balance.response.BonusBalance
import cu.suitetecsa.sdk.android.balance.response.Custom
import cu.suitetecsa.sdk.android.balance.response.DataBalance
import cu.suitetecsa.sdk.android.balance.response.MessagesBalance
import cu.suitetecsa.sdk.android.balance.response.PrincipalBalance
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.balance.response.VoiceBalance
import cu.suitetecsa.sdk.android.kotlin.asDateString
import cu.suitetecsa.sdk.android.kotlin.consultBalance
import cu.suitetecsa.sdk.android.kotlin.ussdExecute
import cu.suitetecsa.sdk.android.model.MainData
import cu.suitetecsa.sdk.android.model.MainSms
import cu.suitetecsa.sdk.android.model.MainVoice
import cu.suitetecsa.sdk.android.model.SimCard
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
    private val simCardsCollector: SimCardCollector,
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
        get() {
            return if (currentSimCardId.isNotBlank()) {
                simCards.firstOrNull {
                    it.serialNumber() == preferences.value.currentSimCardId
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
    var consultType: UssdRequest? = null
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
                    _state.value =
                        BalanceState(currentSimCard = currentSimCard, simCards = simCards)
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
                override fun onRequesting(request: UssdRequest) {
                    this@BalancesViewModel.consultType = request
                    val consultMessage = when (request) {
                        BONUS_BALANCE -> "Consultando Bonos"
                        DATA_BALANCE -> "Consultando Datos"
                        MESSAGES_BALANCE -> "Consultando SMS"
                        PRINCIPAL_BALANCE -> "Consultando Saldo"
                        VOICE_BALANCE -> "Consultando Minutos"
                        CUSTOM -> ""
                    }
                    _state.value = _state.value.copy(consultMessage = consultMessage)
                }

                override fun onSuccess(
                    request: UssdRequest,
                    ussdResponse: UssdResponse
                ) {
                    when (request) {
                        BONUS_BALANCE -> {
                            _state.value = _state.value.copy(
                                bonusCredit = (ussdResponse as BonusBalance).credit,
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

                        DATA_BALANCE -> {
                            _state.value = _state.value.copy(
                                data = MainData(
                                    (ussdResponse as DataBalance).usageBasedPricing,
                                    ussdResponse.data(),
                                    ussdResponse.dataLte(),
                                    ussdResponse.remainingDays()
                                ),
                                mailData = ussdResponse.mailData(),
                                dailyData = ussdResponse.dailyData()
                            )
                        }

                        MESSAGES_BALANCE -> {
                            _state.value = _state.value.copy(
                                sms = MainSms(
                                    (ussdResponse as MessagesBalance).sms(),
                                    ussdResponse.remainingDays()
                                )
                            )
                        }

                        PRINCIPAL_BALANCE -> {
                            _state.value = _state.value.copy(
                                balance = (ussdResponse as PrincipalBalance).balance().toFloat(),
                                activeUntil = ussdResponse.activeUntil().asDateString,
                                mainBalanceDueDate = ussdResponse.dueDate().asDateString,
                            )
                        }

                        VOICE_BALANCE -> {
                            _state.value = _state.value.copy(
                                voice = MainVoice(
                                    (ussdResponse as VoiceBalance).time,
                                    ussdResponse.remainingDays
                                )
                            )
                        }

                        else -> {}
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    throwable.printStackTrace()
                    _state.value = _state.value.copy(
                        errorText = throwable.message,
                        consultMessage = null,
                        loading = false,
                    )
                    canUpdate = true
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(android.Manifest.permission.CALL_PHONE)
    private fun turnUsageBasedPricing(isActive: Boolean) {
        val ussdCode = if (!isActive) {
            "*133*1*1*2${Uri.parse("#")}"
        } else {
            "*133*1*1*1${Uri.parse("#")}"
        }
        currentSimCard?.also {
            it.ussdExecute(
                ussdCode,
                object : ConsultBalanceCallBack {
                    override fun onRequesting(request: UssdRequest) {
                        _state.value = _state.value.copy(
                            consultMessage = if (!isActive) {
                                "Desactivando TPC"
                            } else {
                                "Activando TPC"
                            }
                        )
                    }

                    override fun onSuccess(
                        request: UssdRequest,
                        ussdResponse: UssdResponse
                    ) {
                        when (request) {
                            CUSTOM -> {
                                if ((ussdResponse as Custom).response == UssdResponse.PROCESSING_RESPONSE) {
                                    _state.value.data?.let { data ->
                                        _state.value = _state.value.copy(
                                            data = MainData(
                                                isActive,
                                                data.data,
                                                data.dataLte,
                                                data.remainingDays
                                            )
                                        )
                                    } ?: run {
                                        _state.value = _state.value.copy(
                                            data = MainData(
                                                isActive,
                                                null,
                                                null,
                                                null
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
