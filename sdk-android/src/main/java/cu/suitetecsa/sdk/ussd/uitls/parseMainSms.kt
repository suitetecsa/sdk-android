package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainSms(): MainSms {
    val smsPattern =
        Pattern.compile("""Usted dispone de\s+(?<sms>(\d+))\s+SMS\s+validos por\s+(?<dueDate>(\d+\s+dias))""")
    val matcher = smsPattern.matcher(this.message)
    return if (matcher.find()) {
        MainSms(
            mainSms = matcher.group("sms")?.toInt() ?: 0,
            mainSmsDueDate = matcher.group("dueDate") ?: ""
        )
    } else MainSms(mainSms = 0, mainSmsDueDate = "")
}