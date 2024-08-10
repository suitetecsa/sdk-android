package io.github.suitetecsa.sdk.android.model

@JvmRecord
data class MainBalance(
    val balance: String,
    val data: MainData?,
    val voice: Voice?,
    val sms: Sms?,
    val dailyData: DailyData?,
    val mailData: MailData?,
    val lockDate: String,
    val deletionDate: String
)
