package cu.suitetecsa.sdk.ussd.uitls

fun String.toSeconds() = this.split(":").fold(0L) { acc, s -> acc * 60 + s.toLong() }