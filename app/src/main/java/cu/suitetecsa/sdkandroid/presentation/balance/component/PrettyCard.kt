package cu.suitetecsa.sdkandroid.presentation.balance.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.sdkandroid.ui.theme.RedError
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme

@Composable
fun PrettyCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    backgroundColor: Color? = null,
    content: (@Composable () -> Unit)
) {
    val cardColors = backgroundColor?.let {
        CardDefaults.cardColors(containerColor = it)
    } ?: CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = cardColors
    ) {
        Box {
            if (isLoading) {
                CustomLinearProgressBar(modifier = Modifier.matchParentSize())
            }
            Card(
                modifier = Modifier
                    .padding(6.dp),
                shape = MaterialTheme.shapes.small,
                colors = cardColors
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrettyCardPreview() {
    SDKAndroidTheme {
        PrettyCard(
            modifier = Modifier.padding(16.dp),
            isLoading = true,
            backgroundColor = RedError
        ) {
            Text(text = "Hello", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PrettyCardPreviewDark() {
    SDKAndroidTheme {
        Surface(
            Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            PrettyCard(
                modifier = Modifier.padding(16.dp),
                isLoading = true,
                backgroundColor = RedError
            ) {
                Text(text = "Hello", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
