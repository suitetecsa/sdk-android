package cu.suitetecsa.sdkandroid.presentation.balance

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.sdkandroid.R
import cu.suitetecsa.sdkandroid.domain.model.SimBalance
import cu.suitetecsa.sdkandroid.presentation.balance.component.BalanceHero
import cu.suitetecsa.sdkandroid.presentation.balance.component.BalanceSkeleton
import cu.suitetecsa.sdkandroid.presentation.balance.component.Spinner
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.LongUtils.asRemainingDays
import io.github.suitetecsa.sdk.android.utils.LongUtils.asSizeString
import io.github.suitetecsa.sdk.android.utils.LongUtils.asTimeString
import io.github.suitetecsa.sdk.android.utils.isActive
import java.util.Date

@Composable
fun BalanceRoute(
    onChangeTitle: (String) -> Unit,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit,
    topPadding: PaddingValues,
    balancesViewModel: BalancesViewModel = hiltViewModel(),
) {
    val state = balancesViewModel.state.value

    LaunchedEffect(Unit) {
        onChangeTitle("Balance")
    }

    val onSimCardSelect: (SimCard?) -> Unit = remember {
        { it?.also { simCard -> balancesViewModel.onEvent(BalanceEvent.ChangeSimCard(simCard)) } }
    }
    val onRefresh = remember {
        { balancesViewModel.onEvent(BalanceEvent.FetchAll) }
    }

    val actions: @Composable RowScope.() -> Unit = remember(
        state.simCards, state.currentSimCard, state.loading,
    ) {
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.simCards.size > 1) {
                    SimCardSelector(
                        simCards = state.simCards,
                        currentSimCard = state.currentSimCard,
                        onSimCardSelect = onSimCardSelect,
                        enabled = !state.loading,
                    )
                }
                if (state.currentSimCard != null) {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !state.loading,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(actions) {
        onSetActions(actions)
    }

    BalanceScreen(
        topPadding = topPadding,
        state = state,
        onTurnUsageBasedPricing = { balancesViewModel.onEvent(BalanceEvent.TurnUsageBasedPricing(it)) },
        onRetry = { balancesViewModel.onEvent(BalanceEvent.FetchAll) },
    )
}

@Composable
fun BalanceScreen(
    topPadding: PaddingValues,
    state: BalanceState,
    onTurnUsageBasedPricing: (Boolean) -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(topPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        if (state.loading && state.simBalance != null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            state.loading && state.simBalance == null -> {
                BalanceSkeleton()
            }

            state.error != null && state.simBalance == null -> {
                ErrorState(error = state.error, onRetry = onRetry)
            }

            state.simBalance != null -> {
                BalanceContent(
                    simBalance = state.simBalance,
                    simCard = state.currentSimCard,
                    loading = state.loading,
                    onTurnUsageBasedPricing = onTurnUsageBasedPricing,
                )
            }

            else -> {
                EmptyState(onFetch = onRetry)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BalanceContent(
    simBalance: SimBalance,
    simCard: SimCard?,
    loading: Boolean,
    onTurnUsageBasedPricing: (Boolean) -> Unit,
) {
    BalanceHero(
        simBalance = simBalance,
        simCard = simCard,
        modifier = Modifier.padding(top = 8.dp),
    )

    Spacer(modifier = Modifier.height(24.dp))

    SectionHeader(title = "Planes")
    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlanCard(
                title = "Datos",
                value = simBalance.data?.asSizeString,
                expire = simBalance.dataExpires?.takeIf { it.isActive },
                modifier = Modifier.weight(1f),
            )
            PlanCard(
                title = "Voz",
                value = simBalance.voice?.asTimeString,
                expire = simBalance.voiceExpires?.takeIf { it.isActive },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlanCard(
                title = "SMS",
                value = simBalance.sms?.toString(),
                expire = simBalance.smsExpires?.takeIf { it.isActive },
                modifier = Modifier.weight(1f),
            )
            PlanCard(
                title = "Bolsa diaria",
                value = simBalance.dailyData?.asSizeString,
                expire = simBalance.dailyDataExpires?.takeIf { it.isActive }
                    ?.let { "$it horas" },
                modifier = Modifier.weight(1f),
            )
        }
    }

    val hasBonuses = simBalance.bonusCredit != null || simBalance.bonusData != null ||
        simBalance.dataCu != null || simBalance.bonusUnlimitedDataExpires != null

    if (hasBonuses) {
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(title = "Bonos")
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val bonusEntries = listOfNotNull(
                simBalance.bonusCredit?.let { credit ->
                    PlanCardData(
                        title = "Saldo",
                        value = "$%.2f CUP".format(credit),
                        expire = simBalance.bonusCreditExpires?.time?.asRemainingDays?.let { "$it dias" },
                    )
                },
                simBalance.bonusData?.let { data ->
                    PlanCardData(
                        title = "Datos",
                        value = data.asSizeString,
                        expire = simBalance.bonusDataExpires?.time?.asRemainingDays?.let { "$it dias" },
                    )
                },
                simBalance.dataCu?.let { cu ->
                    PlanCardData(
                        title = "Datos CU",
                        value = cu.asSizeString,
                        expire = simBalance.dataCuExpires?.time?.asRemainingDays?.let { "$it dias" },
                    )
                },
                simBalance.bonusUnlimitedDataExpires?.let { expires ->
                    PlanCardData(
                        title = "Datos Ilimitados",
                        value = "12:00 a.m → 7:00 a.m",
                        expire = "${expires.time.asRemainingDays} dias",
                    )
                },
            )

            bonusEntries.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { item ->
                        PlanCard(
                            title = item.title,
                            value = item.value,
                            expire = item.expire,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    ConsumptionCard(
        consumptionRate = simBalance.consumptionRate,
        enabled = !loading,
        onToggle = onTurnUsageBasedPricing,
    )
}

private data class PlanCardData(
    val title: String,
    val value: String,
    val expire: String?,
)

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun PlanCard(
    title: String,
    value: String?,
    expire: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value ?: "Sin datos",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            expire?.let {
                Text(
                    text = "Vence: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            } ?: Text(
                text = "No activo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun ConsumptionCard(
    consumptionRate: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Tarifa por consumo",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = if (consumptionRate) "Activa" else "Inactiva",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = consumptionRate,
                onCheckedChange = onToggle,
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Error al consultar",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
private fun EmptyState(onFetch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Selecciona una SIM",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Presiona el botón de refrescar para consultar el saldo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onFetch) {
            Text(text = "Consultar")
        }
    }
}

@Composable
fun SimCardSelector(
    simCards: List<SimCard?>,
    currentSimCard: SimCard?,
    onSimCardSelect: (SimCard?) -> Unit,
    enabled: Boolean,
) {
    val simCardIcons = listOf(
        ImageVector.vectorResource(id = R.drawable.sim_one),
        ImageVector.vectorResource(id = R.drawable.sim_two),
        ImageVector.vectorResource(id = R.drawable.sim_three),
    )
    Spinner(
        items = simCards,
        selectedItem = currentSimCard,
        onItemSelect = onSimCardSelect,
        enabled = enabled,
        selectedItemFactory = { simCard, modifier ->
            simCard?.let {
                Row(
                    modifier = modifier
                        .padding(8.dp)
                        .wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = simCardIcons[it.slotIndex],
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = it.displayName ?: "SIM ${it.slotIndex + 1}")
                }
            }
        },
        dropdownItemFactory = { item, _ ->
            item?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = simCardIcons[it.slotIndex],
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = it.displayName ?: "SIM ${it.slotIndex + 1}")
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BalancePreview() {
    SDKAndroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Balance") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )
                },
            ) { paddingValues ->
                BalanceScreen(
                    topPadding = paddingValues,
                    state = BalanceState(
                        simBalance = SimBalance(
                            balance = 123.45f,
                            lockDate = Date(),
                            deletionDate = Date(),
                            consumptionRate = false,
                            data = 2_500_000_000L,
                            dataExpires = "30",
                            voice = 3600L,
                            voiceExpires = "7",
                            sms = 50,
                            smsExpires = "30",
                            dailyData = null,
                            dailyDataExpires = null,
                        ),
                        loading = false,
                    ),
                    onTurnUsageBasedPricing = {},
                    onRetry = {},
                )
            }
        }
    }
}
