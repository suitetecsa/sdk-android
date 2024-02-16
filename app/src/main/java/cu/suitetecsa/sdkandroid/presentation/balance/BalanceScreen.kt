package cu.suitetecsa.sdkandroid.presentation.balance

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.SimCardAlert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.sdk.android.kotlin.asRemainingDays
import cu.suitetecsa.sdk.android.kotlin.asSizeString
import cu.suitetecsa.sdk.android.kotlin.asTimeString
import cu.suitetecsa.sdk.android.model.BonusDataCU
import cu.suitetecsa.sdk.android.model.MainData
import cu.suitetecsa.sdk.android.model.MainSms
import cu.suitetecsa.sdk.android.model.MainVoice
import cu.suitetecsa.sdk.android.model.SimCard
import cu.suitetecsa.sdkandroid.R
import cu.suitetecsa.sdkandroid.presentation.balance.component.ContactsBottomSheet
import cu.suitetecsa.sdkandroid.presentation.balance.component.SheetContent
import cu.suitetecsa.sdkandroid.presentation.balance.component.Spinner
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme

@Composable
fun BalanceRoute(
    onChangeTitle: (String) -> Unit,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit,
    topPadding: PaddingValues,
    balancesViewModel: BalancesViewModel = hiltViewModel(),
) {
    val state = balancesViewModel.state.value

    onSetActions {
        BalanceActions(
            simCards = state.simCards,
            currentSimCard = state.currentSimCard,
            onSimCardSelect = {
                it?.also { simCard ->
                    balancesViewModel.onEvent(BalanceEvent.ChangeSimCard(simCard))
                }
            },
            onBalanceUpdate = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    balancesViewModel.onEvent(
                        BalanceEvent.UpdateBalance
                    )
                } else {
                    Unit
                }
            },
            isSomeTaskRunning = state.loading
        )
    }

    onChangeTitle(state.consultMessage ?: "Balance")

    BalanceScreen(
        topPadding = topPadding,
        state = state,
        onTurnUsageBasedPricing = { balancesViewModel.onEvent(BalanceEvent.TurnUsageBasedPricing(it)) },
        collectContacts = { balancesViewModel.onEvent(BalanceEvent.CollectContacts) }
    )
}

@Composable
fun BalanceScreen(
    topPadding: PaddingValues,
    state: BalanceState,
    onTurnUsageBasedPricing: (Boolean) -> Unit,
    collectContacts: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var isSheetOpen by remember { mutableStateOf(false) }
        Box(modifier = Modifier.height(topPadding.calculateTopPadding())) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BalanceInfo(
                state = state,
                onTurnUsageBasedPricing = onTurnUsageBasedPricing
            )
        } else {
            Text(text = "Not supported")
        }
        Text(text = state.errorText ?: "")
        Button(
            onClick = {
                collectContacts()
                isSheetOpen = true
            },
            enabled = state.contacts.isEmpty()
        ) {
            Text(text = "Collect Contacts")
        }
        ContactsBottomSheet(contacts = state.contacts, isSheetOpen, setSheetOpen = { isSheetOpen = it })
    }
}

@Composable
fun BalanceInfo(
    state: BalanceState,
    onTurnUsageBasedPricing: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Saldo:")
            Text(
                text = "$%.2f CUP".format(state.balance),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "Expira: ${state.activeUntil ?: "???"}")
                Text(text = "Bence: ${state.mainBalanceDueDate ?: "???"}")
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tarifa por consumo")
                Switch(
                    checked = state.data?.usageBasedPricing ?: true,
                    onCheckedChange = onTurnUsageBasedPricing,
                    enabled = !state.loading
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            PlansSection(state = state)
            Spacer(modifier = Modifier.padding(4.dp))
            BonusSection(state = state)
        }
    }
}

@Composable
fun PlansSection(state: BalanceState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        state.data?.let { data ->
            val dataCount = if (data.data != null && data.dataLte != null) {
                "${data.data!!.asSizeString} + ${data.dataLte!!.asSizeString} LTE"
            } else if (data.data != null) {
                data.data!!.asSizeString
            } else {
                "${data.dataLte!!.asSizeString} LTE"
            }
            DataPlan(
                planTitle = "Datos",
                dataCount = dataCount,
                dataExpire = data.remainingDays?.let { "$it días" }
            )
        }
        state.voice?.let { voice ->
            DataPlan(
                planTitle = "Voz",
                dataCount = voice.seconds.asTimeString,
                dataExpire = voice.remainingDays?.let { "$it días" }
            )
        }
        state.sms?.let { sms ->
            DataPlan(
                planTitle = "SMS",
                dataCount = "${sms.sms} SMS",
                dataExpire = sms.remainingDays?.let { "$it días" }
            )
        }
        state.dailyData?.let { dailyData ->
            DataPlan(
                planTitle = "Bolsa diaria",
                dataCount = dailyData.data.asSizeString,
                dataExpire = dailyData.remainingHours()?.let { "$it horas" }
            )
        }
        state.mailData?.let { mailData ->
            DataPlan(
                planTitle = "Bolsa correo",
                dataCount = mailData.data.asSizeString,
                dataExpire = mailData.remainingDays()?.let { "$it días" }
            )
        }
    }
}

@Composable
fun BonusSection(state: BalanceState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        state.bonusCredit?.let { bonusCredit ->
            DataPlan(
                planTitle = "Saldo",
                dataCount = "$%.2f CUP".format(bonusCredit.balance),
                dataExpire = "${bonusCredit.dueDate.asRemainingDays} dias"
            )
        }
        state.bonusData?.let { bonusData ->
            val dataCount =
                if (bonusData.data != null && bonusData.dataLte != null) {
                    "${bonusData.data!!.asSizeString} + " +
                            "${bonusData.dataLte!!.asSizeString} LTE"
                } else if (bonusData.data != null) {
                    bonusData.data!!.asSizeString
                } else {
                    "${bonusData.dataLte!!.asSizeString} LTE"
                }
            DataPlan(
                planTitle = "Datos",
                dataCount = dataCount,
                dataExpire = "${bonusData.dueDate.asRemainingDays} dias"
            )
        }
        state.bonusDataCU?.let { bonusDataCU ->
            DataPlan(
                planTitle = "Datos CU",
                dataCount = bonusDataCU.data.asSizeString,
                dataExpire = "${bonusDataCU.dueDate.asRemainingDays} dias"
            )
        }
        state.bonusUnlimitedData?.let { bonusUnlimitedData ->
            DataPlan(
                planTitle = "Datos Ilimitados",
                dataCount = "12:00 a.m -> 7:00 a.m",
                dataExpire = "${bonusUnlimitedData.dueDate.asRemainingDays} dias"
            )
        }
    }
}

@Preview(showBackground = true, device = "id:Nexus One")
@Composable
private fun BalanceInfoPreview() {
    SDKAndroidTheme { Surface { BalanceInfo(BalanceState()) {} } }
}

@Suppress("MagicNumber")
@Preview(
    showBackground = true,
    device = "id:Nexus One",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BalanceInfoPreviewDark() {
    SDKAndroidTheme {
        Surface {
            BalanceInfo(
                BalanceState(
                    balance = 123.45f,
                    activeUntil = "10/10/2022",
                    mainBalanceDueDate = "10/10/2022",
                    data = MainData(false, 8345369725, 29376382496, 25),
                    bonusDataCU = BonusDataCU(236975200, 55665L),
                    voice = MainVoice(586314L, 25),
                    sms = MainSms(50, 25)
                )
            ) {}
        }
    }
}

@Composable
fun DataPlan(
    planTitle: String,
    dataCount: String,
    dataExpire: String?
) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = planTitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(text = dataCount)
        dataExpire?.let {
            Text(text = "Vence: $it")
        } ?: run {
            Text(text = "No activo")
        }
    }
}

@Composable
fun BalanceActions(
    simCards: List<SimCard?>,
    currentSimCard: SimCard?,
    onSimCardSelect: (SimCard?) -> Unit,
    onBalanceUpdate: () -> Unit,
    isSomeTaskRunning: Boolean
) {
    if (simCards.isNotEmpty()) {
        val simCardIcons = listOf(
            ImageVector.vectorResource(id = R.drawable.sim_one),
            ImageVector.vectorResource(id = R.drawable.sim_two),
            ImageVector.vectorResource(id = R.drawable.sim_three)
        )
        if (simCards.size > 1) {
            Spinner(
                items = simCards,
                selectedItem = currentSimCard,
                onItemSelect = onSimCardSelect,
                enabled = !isSomeTaskRunning,
                selectedItemFactory = { modifier, item ->
                    Row(
                        modifier = modifier
                            .padding(8.dp)
                            .wrapContentSize()
                    ) {
                        item?.let {
                            Icon(
                                imageVector = simCardIcons[it.slotIndex],
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                dropdownItemFactory = { item, _ ->
                    item?.let {
                        Row {
                            Icon(
                                imageVector = simCardIcons[it.slotIndex],
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = " ${it.displayName}")
                        }
                    }
                }
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IconButton(onClick = onBalanceUpdate, enabled = !isSomeTaskRunning) {
                Icon(imageVector = Icons.Outlined.Autorenew, contentDescription = "Update")
            }
        }
    } else {
        Icon(imageVector = Icons.Outlined.SimCardAlert, contentDescription = null)
    }
}
