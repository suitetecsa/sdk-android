package cu.suitetecsa.sdk.ussd.uitls

private const val TIME_FORMAT = "%02d:%02d:%02d"
private const val SECONDS_IN_HOURS = 3600
private const val SECONDS_IN_MINUTES = 60

fun Long.toTimeString() = String.format(
    TIME_FORMAT,
    this / SECONDS_IN_HOURS,
    this % SECONDS_IN_HOURS / SECONDS_IN_MINUTES,
    this % SECONDS_IN_MINUTES
)
