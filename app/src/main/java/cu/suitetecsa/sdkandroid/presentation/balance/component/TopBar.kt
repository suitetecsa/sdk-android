package cu.suitetecsa.sdkandroid.presentation.balance.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    actions: @Composable (RowScope.() -> Unit) = { }
) {
    TopAppBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.small),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(),
                text = title
            )
        },
        actions = actions
    )
}

@Preview(showBackground = true)
@Composable
private fun TopBarPreview() {
    SDKAndroidTheme {
        TopBar(
            title = "Cubacel",
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TopBarPreviewDark() {
    SDKAndroidTheme {
        TopBar(
            title = "Cubacel",
        )
    }
}
