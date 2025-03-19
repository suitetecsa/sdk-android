package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import androidx.annotation.RequiresPermission

private const val DEFAULT_DELAY = 2000L
private const val MAX_RETRIES: Int = 3
private const val RETRY_DELAY_MILLIS: Long = 2000

interface UssdRequestSender {
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun send(callback: RequestCallback)

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun send(ussdCode: String, callback: RequestCallback)
    class Builder {
        private var delayMillis: Long? = null
        private var maxRetries: Int? = null
        private var retryDelayMillis: Long? = null

        @Suppress("unused")
        fun withDelay(delay: Long): Builder {
            delayMillis = delay
            return this
        }

        @Suppress("unused")
        fun withMaxRetries(maxRetries: Int): Builder {
            this.maxRetries = maxRetries
            return this
        }

        @Suppress("unused")
        fun withRetryDelay(retryDelay: Long): Builder {
            retryDelayMillis = retryDelay
            return this
        }

        fun build(): UssdRequestSender {
            return UssdRequestSenderImpl(
                initialDelayMillis = delayMillis ?: DEFAULT_DELAY,
                maxRetries = maxRetries ?: MAX_RETRIES,
                retryDelayMillis = retryDelayMillis ?: RETRY_DELAY_MILLIS
            )
        }
    }
}
