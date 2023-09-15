package cu.suitetecsa.sdk.ussd.datasources

interface UssdRequestSender {
    suspend fun send(ussdCode: String): CharSequence
}
