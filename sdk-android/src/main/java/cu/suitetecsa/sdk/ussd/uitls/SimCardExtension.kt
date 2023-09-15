package cu.suitetecsa.sdk.ussd.uitls

import android.Manifest
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.UssdApi
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
suspend fun SimCard.sendUssdRequest(ussdCode: String): UssdResponse {
    val ussdApi = UssdApi.Builder(this.telephony).build()
    return ussdApi.sendUssdRequest(ussdCode)
}
