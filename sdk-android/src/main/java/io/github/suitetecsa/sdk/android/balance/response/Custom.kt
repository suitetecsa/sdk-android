package io.github.suitetecsa.sdk.android.balance.response

/**
 * Clase para representar una respuesta USSD personalizada
 */
@JvmRecord
data class Custom(val response: String) : UssdResponse
