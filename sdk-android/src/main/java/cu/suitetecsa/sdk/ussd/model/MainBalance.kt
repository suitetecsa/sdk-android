package cu.suitetecsa.sdk.ussd.model

data class MainBalance(
    val credit: Float?,
    var data: MainData,
    var voice: MainVoice,
    var sms: MainSms,
    var dailyData: DailyData,
    var mailData: MailData,
    val activeUntil: String?,
    val mainBalanceDueDate: String?
)