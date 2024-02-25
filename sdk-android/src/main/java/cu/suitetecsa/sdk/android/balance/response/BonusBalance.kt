package cu.suitetecsa.sdk.android.balance.response

import cu.suitetecsa.sdk.android.model.BonusCredit
import cu.suitetecsa.sdk.android.model.BonusData
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData
import cu.suitetecsa.sdk.android.model.DataCu
import cu.suitetecsa.sdk.android.model.Sms
import cu.suitetecsa.sdk.android.model.Voice

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
