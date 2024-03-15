package cu.suitetecsa.sdk.android

import android.content.Context
import cu.suitetecsa.sdk.android.model.Contact

/**
 * Interface for collecting contact information from the device.
 */
interface ContactsCollector {
    /**
     * Collects contact information from the device.
     *
     *
     * Note: This method requires [android.Manifest.permission.READ_CONTACTS] permission.
     * Make sure to check and request this permission at runtime before calling collect.
     *
     *
     * @return A list of [Contact] objects containing the collected contact information.
     */
    fun collect(): List<Contact>

    /**
     * Builder class for creating instances of [ContactsCollector].
     */
    class Builder {
        /**
         * Builds a new [ContactsCollector] instance.
         *
         * @param context the [Context] used to access the contacts.
         * @return a new instance of [ContactsCollectorImpl].
         */
        fun build(context: Context): ContactsCollector {
            return ContactsCollectorImpl(context)
        }
    }
}
