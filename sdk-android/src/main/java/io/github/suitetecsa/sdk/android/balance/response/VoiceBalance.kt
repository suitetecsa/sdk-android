package io.github.suitetecsa.sdk.android.balance.response

/**
 * Clase para representar la respuesta de saldo de voz
 */
@JvmRecord
data class VoiceBalance(val data: String, val expires: String) : UssdResponse
