package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainVoice
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainVoice(): MainVoice {
    val voicePattern =
        ("""Usted dispone de\s+(?<voice>(\d+:\d{2}:\d{2}))\s+MIN(\s+no activos)?""" +
                """(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?""")
            .toRegex()

    val (voice, remainingDays) = voicePattern.find(this.message)?.let { matchResult ->
        Pair(
            matchResult.groups["voice"]?.value?.toSeconds(),
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    } ?: Pair(null, null)

    return MainVoice(
        mainVoice = voice,
        remainingDays = remainingDays
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    val response = UssdResponse(
        message = "Usted dispone de 07:37:49 MIN validos por 24 dias"
    )
    println(response.parseMainVoice())
}