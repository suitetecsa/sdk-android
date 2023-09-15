package cu.suitetecsa.sdk.ussd.datasources

import cu.suitetecsa.sdk.ussd.model.UssdResponse

internal class UssdResponseHandlerImpl : UssdResponseHandler {
    // This method takes a CharSequence as an argument and returns a UssdResponse
    override fun handle(response: CharSequence): UssdResponse {
        // Convert the CharSequence to a String
        return UssdResponse(response.toString())
    }
}
