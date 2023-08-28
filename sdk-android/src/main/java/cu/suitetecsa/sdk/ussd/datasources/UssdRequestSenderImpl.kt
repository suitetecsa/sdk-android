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

@RequiresApi(Build.VERSION_CODES.O)
internal class UssdRequestSenderImpl(private val telephonyManager: TelephonyManager) : UssdRequestSender {
    //Suppress lint warning for missing permission
    @SuppressLint("MissingPermission")
    //This method is used to send a USSD request
    override suspend fun send(ussdCode: String): CharSequence {
        //This suspend function is used to send a USSD request
        return suspendCancellableCoroutine { continuation ->
            //This object is used to send a USSD request
            val callback = object : TelephonyManager.UssdResponseCallback() {
                //This method is used to receive a USSD response
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    //This method is used to resume the continuation
                    continuation.resume(response)
                }

                //This method is used to receive a USSD response failed
                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    //This method is used to resume the continuation with an exception
                    continuation.resumeWithException(UssdException("USSD request failed with code $failureCode"))
                }
            }

            //This sends the USSD request
            telephonyManager.sendUssdRequest(ussdCode, callback, Handler(Looper.getMainLooper()))

            //This method is used to cancel the continuation
            continuation.invokeOnCancellation {}
        }
    }
}
