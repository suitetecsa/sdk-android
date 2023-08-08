package cu.suitetecsa.sdk.ussd.datasources

import android.annotation.SuppressLint
import android.os.Build
import android.os.Looper
import android.os.Handler
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.UssdException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class UssdRequestSenderImpl(private val telephonyManager: TelephonyManager) : UssdRequestSender {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override suspend fun send(ussdCode: String): CharSequence {
        return suspendCancellableCoroutine { continuation ->
            val callback = object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    continuation.resume(response)
                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    continuation.resumeWithException(UssdException("USSD request failed with code $failureCode"))
                }
            }

            telephonyManager.sendUssdRequest(ussdCode, callback, Handler(Looper.getMainLooper()))

            continuation.invokeOnCancellation {}
        }
    }
}
