package io.github.suitetecsa.sdk.android.balance.response

/**
 * Clase para representar la respuesta de saldo de mensajes
 */
@JvmRecord
data class MessagesBalance(val data: String, val expires: String) : UssdResponse
