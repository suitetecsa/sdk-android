package cu.suitetecsa.sdk.android.model;

public record MainBalance(double balance, MainData data, MainVoice voice, MainSms sms,
                          DailyData dailyData, MailData mailData, long activeUntil, long dueDate) {
}
