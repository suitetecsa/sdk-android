package cu.suitetecsa.sdk.ussd.model

data class MainBalance(
    val credit: Float,
    var data: MainData,
    var voice: MainVoice,
    var sms: MainSms,
    val activeUntil: String,
    val mainBalanceDueDate: String
)