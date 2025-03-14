package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import androidx.annotation.RequiresPermission

private const val DEFAULT_DILAY = 2000L

/**
 * Interfaz para ejecutar una solicitud de saldo USSD
 */
interface UssdRequestSender {
    /**
     * Método para ejecutar una solicitud de saldo USSD con el callback proporcionado
     * @param callback a RequestCallback instance
     */
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun send(callback: RequestCallback)

    /**
     * Método para ejecutar una solicitud de saldo USSD con el código USSD y el callback proporcionados
     * @param ussdCode ussdCode to send request
     * @param callback a RequestCallback instance
     */
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun send(ussdCode: String, callback: RequestCallback)
    class Builder {
        private var delayMillis: Long? = null

        @Suppress("unused")
        fun withDelay(delay: Long): Builder {
            delayMillis = delay
            return this
        }

        fun build(): UssdRequestSender {
            return UssdRequestSenderImpl(delayMillis ?: DEFAULT_DILAY)
        }
    }
}
