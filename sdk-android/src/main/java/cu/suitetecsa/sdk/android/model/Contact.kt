package cu.suitetecsa.sdk.android.model

import cu.suitetecsa.sdk.android.utils.extractShortNumber

data class Contact(val name: String, val phoneNumber: String, val photoUri: String?) {
    val shortNumber: String?
        get() = extractShortNumber(phoneNumber)
}
