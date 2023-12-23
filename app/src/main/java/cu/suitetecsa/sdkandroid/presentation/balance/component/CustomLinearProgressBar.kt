package cu.suitetecsa.sdkandroid.presentation.balance.component

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomLinearProgressBar(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    progress: Float? = null
) {
    if (progress != null) {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = Color.Transparent
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = Color.Transparent
        )
    }

}