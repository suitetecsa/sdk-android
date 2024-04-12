package io.github.suitetecsa.sdk.android.balance.response

import io.github.suitetecsa.sdk.android.model.BonusCredit
import io.github.suitetecsa.sdk.android.model.BonusData
import io.github.suitetecsa.sdk.android.model.BonusUnlimitedData
import io.github.suitetecsa.sdk.android.model.DataCu
import io.github.suitetecsa.sdk.android.model.Sms
import io.github.suitetecsa.sdk.android.model.Voice

/**
 * Clase para representar la respuesta de saldo de bonificación
 */
@JvmRecord
data class BonusBalance(
    val credit: BonusCredit?,
    val unlimitedData: BonusUnlimitedData?,
    val data: BonusData?,
    val dataCu: DataCu?,
    val voice: Voice?,
    val sms: Sms?
) : UssdResponse
