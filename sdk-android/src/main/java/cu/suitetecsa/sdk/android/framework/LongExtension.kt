package cu.suitetecsa.sdk.android.framework

private const val TIME_FORMAT = "%02d:%02d:%02d"
private const val SECONDS_IN_HOURS = 3600
private const val SECONDS_IN_MINUTES = 60

/**
 * Converts a duration in seconds to a time string representation in the format "HH:MM:SS".
 *
 * @return The duration formatted as a time string.
 */
fun Long.toTimeString(): String = String.format(
    TIME_FORMAT,
    this / SECONDS_IN_HOURS,
    this % SECONDS_IN_HOURS / SECONDS_IN_MINUTES,
    this % SECONDS_IN_MINUTES
)
