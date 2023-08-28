package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainSms(): MainSms {
    val smsRegex =
        """Usted dispone de\s+(?<sms>(\d+))\s+SMS(\s+no activos)?(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?(\.)?"""
            .toRegex()

    val (sms, remainingDays) = smsRegex.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["sms"]?.value?.toInt(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MainSms(mainSms = sms, remainingDays = remainingDays)
}
