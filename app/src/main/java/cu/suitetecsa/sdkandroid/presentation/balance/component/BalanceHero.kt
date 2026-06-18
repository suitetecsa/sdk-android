package cu.suitetecsa.sdkandroid.presentation.balance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme
import cu.suitetecsa.sdkandroid.domain.model.SimBalance
import io.github.suitetecsa.sdk.android.model.SimCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BalanceHero(
    simBalance: SimBalance,
    simCard: SimCard?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = simCard?.phoneNumber ?: "---",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Saldo disponible",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$%.2f CUP".format(simBalance.balance),
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Bloqueo: ${simBalance.lockDate.format()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                )
                Text(
                    text = "Vence: ${simBalance.deletionDate.format()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                )
            }
        }
    }
}

private fun Date.format(): String =
    SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(this)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BalanceHeroDefaultPreview() {
    SDKAndroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BalanceHero(
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
                simCard = null,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BalanceHeroHighBalancePreview() {
    SDKAndroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BalanceHero(
                simBalance = SimBalance(
                    balance = 9999.99f,
                    lockDate = Date(),
                    deletionDate = Date(),
                    consumptionRate = false,
                    data = null,
                    dataExpires = null,
                    voice = null,
                    voiceExpires = null,
                    sms = null,
                    smsExpires = null,
                    dailyData = null,
                    dailyDataExpires = null,
                ),
                simCard = null,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BalanceHeroZeroBalancePreview() {
    SDKAndroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            BalanceHero(
                simBalance = SimBalance(
                    balance = 0.00f,
                    lockDate = Date(),
                    deletionDate = Date(),
                    consumptionRate = false,
                    data = null,
                    dataExpires = null,
                    voice = null,
                    voiceExpires = null,
                    sms = null,
                    smsExpires = null,
                    dailyData = null,
                    dailyDataExpires = null,
                ),
                simCard = null,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
