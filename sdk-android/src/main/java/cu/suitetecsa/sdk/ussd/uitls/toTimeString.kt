package cu.suitetecsa.sdk.ussd.uitls

fun Long.toTimeString() = String.format("%02d:%02d:%02d", this / 3600, this % 3600 / 60, this % 60)