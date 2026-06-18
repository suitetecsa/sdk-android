package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import java.util.Date

/**
 * Clase para representar la respuesta de saldo principal
 */
@JvmRecord
data class PrincipalBalance(
    val balance: Float,
    val blockDate: Date,
    val deletionDate: Date,
    val consults: List<UssdRequest>
) : UssdResponse
