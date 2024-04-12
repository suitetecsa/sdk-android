package io.github.suitetecsa.sdk.android.balance.response

/**
 * Clase para representar la respuesta de saldo de mensajes
 */
@JvmRecord
data class MessagesBalance(val sms: Long, val remainingDays: Int?) : UssdResponse
