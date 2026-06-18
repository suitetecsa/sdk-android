package cu.suitetecsa.sdkandroid.domain.model

import java.util.Date

data class SimBalance(
    val balance: Float,
    val lockDate: Date,
    val deletionDate: Date,
    val consumptionRate: Boolean,
    val data: Long?,
    val dataExpires: String?,
    val voice: Long?,
    val voiceExpires: String?,
    val sms: Int?,
    val smsExpires: String?,
    val dailyData: Long?,
    val dailyDataExpires: String?,
    val bonusCredit: Float? = null,
    val bonusCreditExpires: Date? = null,
    val bonusData: Long? = null,
    val bonusDataExpires: Date? = null,
    val dataCu: Long? = null,
    val dataCuExpires: Date? = null,
    val bonusUnlimitedDataExpires: Date? = null,
)
