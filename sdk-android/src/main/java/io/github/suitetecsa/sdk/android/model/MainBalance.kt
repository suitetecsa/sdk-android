package io.github.suitetecsa.sdk.android.model

import java.util.Date

@JvmRecord
data class MainBalance(
    val balance: Float,
    val data: Long?,
    val voice: Long?,
    val sms: Int?,
    val dailyData: Long?,
    val lockDate: Date,
    val deletionDate: Date
)
