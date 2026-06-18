package io.github.suitetecsa.sdk.android.balance

import android.Manifest
import androidx.annotation.RequiresPermission

interface UssdRequestSender {
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun send(ussdCode: String, callback: RequestCallback)

    class Builder {
        fun build(): UssdRequestSender = UssdRequestSenderImpl()
    }
}
