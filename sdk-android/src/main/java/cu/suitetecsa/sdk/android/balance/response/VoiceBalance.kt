package cu.suitetecsa.sdk.android.balance.response

/**
 * Clase para representar la respuesta de saldo de voz
 */
@JvmRecord
data class VoiceBalance(val seconds: Long, val remainingDays: Int?) : UssdResponse
