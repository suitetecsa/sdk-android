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

class UssdApi private constructor(
    private val requestSender: UssdRequestSender,
    private val responseHandler: UssdResponseHandler
) {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    suspend fun sendUssdRequest(ussdCode: String): UssdResponse {
        val response = requestSender.send(ussdCode)
        return responseHandler.handle(response)
    }

    class Builder(private val telephonyManager: TelephonyManager) {
        private var ussdRequestSender: UssdRequestSender? = null
        private var ussdResponseHandler: UssdResponseHandler? = null

        fun requestSender(requestSender: UssdRequestSender): Builder {
            this.ussdRequestSender = requestSender
            return this
        }

        fun responseHandler(responseHandler: UssdResponseHandler): Builder {
            this.ussdResponseHandler = responseHandler
            return this
        }

        fun build(): UssdApi {
            val requestSender = ussdRequestSender ?: createDefaultRequestSender(telephonyManager)
            val responseHandler = ussdResponseHandler ?: createDefaultResponseHandler()
            return UssdApi(requestSender, responseHandler)
        }

        private fun createDefaultRequestSender(telephonyManager: TelephonyManager): UssdRequestSender {
            return UssdRequestSenderImpl(telephonyManager)
        }

        private fun createDefaultResponseHandler(): UssdResponseHandler {
            return UssdResponseHandlerImpl()
        }
    }

    companion object {
        // Método estático para obtener una instancia del builder
        fun builder(telephonyManager: TelephonyManager): Builder {
            return Builder(telephonyManager)
        }
    }
}
