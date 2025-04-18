package cu.suitetecsa.sdkandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.arr.bugsend.BugSend
import cu.suitetecsa.sdkandroid.presentation.balance.BalanceRoute
import cu.suitetecsa.sdkandroid.presentation.balance.component.TopBar
import cu.suitetecsa.sdkandroid.ui.theme.SDKAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

private const val RequestCode = 50

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BugSend(this)
            .setTitle(getString(R.string.bug_send_title))
            .setIcon(R.drawable.outline_bug_report_24)
            .setMessage(getString(R.string.bug_send_message))
            .setEmail(BuildConfig.SUPPORT_EMAIL)
            .setSubject("REPORT/PortalUsuario")
            .setExtraText("App Version: ${BuildConfig.VERSION_NAME}")
            .show()

        if (checkPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                mutableListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS,
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) add(Manifest.permission.READ_PHONE_NUMBERS)
                }.toTypedArray(),
                RequestCode
            )
            return
        } else {
            setContent {
                SDKAndroidTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var actions: @Composable (RowScope.() -> Unit) by remember { mutableStateOf({}) }
                        var title: String by remember { mutableStateOf("Balance") }
                        Scaffold(
                            topBar = { TopBar(title, actions) }
                        ) { paddingValues ->
                            BalanceRoute(
                                onChangeTitle = { title = it },
                                onSetActions = { actions = it },
                                topPadding = paddingValues,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED
    }
}
