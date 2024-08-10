package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest

/**
 * Clase para representar la respuesta de saldo principal
 */
@JvmRecord
data class PrincipalBalance(
    val balance: String,
    val blockDate: String,
    val deletionDate: String,
    val consults: List<UssdRequest>
) : UssdResponse
