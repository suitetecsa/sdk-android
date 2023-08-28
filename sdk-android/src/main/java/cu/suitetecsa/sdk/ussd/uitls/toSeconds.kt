package cu.suitetecsa.sdk.ussd.uitls

private const val SECONDS_IN_MINUTES = 60

fun String.toSeconds() = this.split(":").fold(0L) { acc, s -> acc * SECONDS_IN_MINUTES + s.toLong() }