package cu.suitetecsa.sdk.android.framework.extensions

import cu.suitetecsa.sdk.android.framework.UssdResponse.VoiceBalance
import cu.suitetecsa.sdk.android.framework.toSeconds

/**
 * Parses the main voice balance from a given CharSequence and returns a UssdResponse.VoiceBalance object.
 *
 * @return The parsed main voice balance as a UssdResponse.VoiceBalance object, or null if the data cannot be parsed.
 */
fun CharSequence.parseMainVoice(): VoiceBalance? {
    // Regular expression to match the voice balance pattern
    val voicePattern =
        (
            """Usted dispone de\s+(?<voice>(\d+:\d{2}:\d{2}))\s+MIN(\s+no activos)?""" +
                """(\s+validos por\s+(?<dueDate>(\d+))\s+dias)?"""
            )
            .toRegex()

    // Parse the voice balance and due date
    return voicePattern.find(this)?.let { matchResult ->
        VoiceBalance(
            matchResult.groups["voice"]?.value?.toSeconds()!!,
            matchResult.groups["dueDate"]?.value?.toInt()
        )
    }
}
