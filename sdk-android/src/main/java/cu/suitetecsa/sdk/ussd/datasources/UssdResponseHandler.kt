package cu.suitetecsa.sdk.ussd.datasources

import cu.suitetecsa.sdk.ussd.model.UssdResponse

interface UssdResponseHandler {
    fun handle(response: CharSequence): UssdResponse
}
