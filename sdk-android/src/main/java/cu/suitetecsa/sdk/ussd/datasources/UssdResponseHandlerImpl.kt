package cu.suitetecsa.sdk.ussd.datasources

import cu.suitetecsa.sdk.ussd.model.UssdResponse

internal class UssdResponseHandlerImpl : UssdResponseHandler {
    override fun handle(response: CharSequence): UssdResponse {
        return UssdResponse(response.toString())
    }
}