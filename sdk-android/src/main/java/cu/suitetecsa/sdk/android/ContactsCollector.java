package cu.suitetecsa.sdk.android;

import android.content.Context;

import java.util.List;

import cu.suitetecsa.sdk.android.model.Contact;

/**
 * Interface for collecting contact information from the device.
 */
public interface ContactsCollector {
    /**
     * Collects contact information from the device.
     * <p>
     * Note: This method requires {@link android.Manifest.permission#READ_CONTACTS} permission.
     * Make sure to check and request this permission at runtime before calling collect.
     * </p>
     *
     * @return A list of {@link Contact} objects containing the collected contact information.
     */
    List<Contact> collect();

    /**
     * Builder class for creating instances of {@link ContactsCollector}.
     */
    class Builder {
        /**
         * Builds a new {@link ContactsCollector} instance.
         *
         * @param context the {@link Context} used to access the contacts.
         * @return a new instance of {@link ContactsCollectorImpl}.
         */
        public ContactsCollector build(Context context) {
            return new ContactsCollectorImpl(context);
        }
    }
}
