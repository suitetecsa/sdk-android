package cu.suitetecsa.sdk.ussd

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.sim.SimCardsAPI
import cu.suitetecsa.sdk.ussd.datasources.UssdRequestSender
import cu.suitetecsa.sdk.ussd.datasources.UssdRequestSenderImpl
import cu.suitetecsa.sdk.ussd.datasources.UssdResponseHandler
import cu.suitetecsa.sdk.ussd.datasources.UssdResponseHandlerImpl
import cu.suitetecsa.sdk.ussd.model.UssdResponse

@RequiresApi(Build.VERSION_CODES.O)
class UssdApi private constructor(
    private val requestSender: UssdRequestSender,
    private val responseHandler: UssdResponseHandler
) {
    // This method sends a USSD request to the phone
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    suspend fun sendUssdRequest(ussdCode: String): UssdResponse {
        // Send the request to the phone
        val response = requestSender.send(ussdCode)
        // Handle the response
        return responseHandler.handle(response)
    }

    // This class is used to build a UssdApi
    class Builder(private val telephonyManager: TelephonyManager) {
        // This field stores the request sender
        private var ussdRequestSender: UssdRequestSender? = null
        // This field stores the response handler
        private var ussdResponseHandler: UssdResponseHandler? = null

        // This method sets the request sender
        fun requestSender(requestSender: UssdRequestSender): Builder {
            this.ussdRequestSender = requestSender
            return this
        }

        // This method sets the response handler
        fun responseHandler(responseHandler: UssdResponseHandler): Builder {
            this.ussdResponseHandler = responseHandler
            return this
        }

        // This method builds a UssdApi
        fun build(): UssdApi {
            // Set the request sender
            val requestSender = ussdRequestSender?: createDefaultRequestSender(telephonyManager)
            // Set the response handler
            val responseHandler = ussdResponseHandler?: createDefaultResponseHandler()
            // Return the UssdApi
            return UssdApi(requestSender, responseHandler)
        }

        // This method creates the default request sender
        private fun createDefaultRequestSender(telephonyManager: TelephonyManager): UssdRequestSender {
            return UssdRequestSenderImpl(telephonyManager)
        }

        // This method creates the default response handler
        private fun createDefaultResponseHandler(): UssdResponseHandler {
            return UssdResponseHandlerImpl()
        }
    }

    companion object {
        // This method creates a builder for a UssdApi
        fun builder(telephonyManager: TelephonyManager): Builder {
            // Return the builder
            return Builder(telephonyManager)
        }
    }
}
