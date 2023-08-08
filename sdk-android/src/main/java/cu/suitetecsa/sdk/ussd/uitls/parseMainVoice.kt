package cu.suitetecsa.sdk.ussd.uitls

import android.os.Build
import androidx.annotation.RequiresApi
import cu.suitetecsa.sdk.ussd.model.MainVoice
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
fun UssdResponse.parseMainVoice(): MainVoice {
    val voicePattern =
        Pattern.compile("""Usted dispone de\s+(?<voice>(\d+:\d{2}:\d{2}))\s+MIN\s+validos por\s+(?<dueDate>(\d+\s+dias))""")
    val matcher = voicePattern.matcher(this.message)
    return if (matcher.find()) {
        MainVoice(
            mainVoice = matcher.group("voice")?.toSeconds() ?: 0L,
            mainVoiceDueDate = matcher.group("dueDate") ?: ""
        )
    } else MainVoice(mainVoice = 0L, mainVoiceDueDate = "")
}