package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest

/**
 * Clase para representar la respuesta de saldo principal
 */
@JvmRecord
data class PrincipalBalance(
    val balance: Double,
    val activeUntil: Long,
    val dueDate: Long,
    val consults: List<UssdRequest>
) : UssdResponse
