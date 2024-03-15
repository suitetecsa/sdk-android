package cu.suitetecsa.sdk.android.model

@JvmRecord
data class MainBalance(
    val balance: Double,
    val data: MainData?,
    val voice: Voice?,
    val sms: Sms?,
    val dailyData: DailyData?,
    val mailData: MailData?,
    val activeUntil: Long,
    val dueDate: Long
)
