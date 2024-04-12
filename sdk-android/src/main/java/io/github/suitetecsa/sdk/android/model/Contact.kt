package io.github.suitetecsa.sdk.android.model

import io.github.suitetecsa.sdk.android.utils.extractShortNumber

data class Contact(val name: String, val phoneNumber: String, val photoUri: String?) {
    val shortNumber: String?
        get() = extractShortNumber(phoneNumber)
}
