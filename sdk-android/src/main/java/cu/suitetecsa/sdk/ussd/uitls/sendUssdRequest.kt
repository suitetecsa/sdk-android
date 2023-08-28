package cu.suitetecsa.sdk.ussd.uitls

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.UssdApi
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
suspend fun SimCard.sendUssdRequest(context: Context, ussdCode: String): UssdResponse {
    val manager = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
        .createForSubscriptionId(subscriptionId)
    val ussdApi = UssdApi.builder(manager).build()
    return ussdApi.sendUssdRequest(ussdCode)
}