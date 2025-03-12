package io.github.suitetecsa.sdk.android

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import io.github.suitetecsa.sdk.android.model.Contact

/**
 * Implementation of [ContactsCollector] for collecting contact information from the device.
 */
internal class ContactsCollectorImpl(private val context: Context) : ContactsCollector {

    /**
     * Collects contact information from the device.
     *
     * This method retrieves contact names, phone numbers, and photo URIs from the device's
     * contacts provider.
     *
     * Note: This method requires the [android.Manifest.permission.READ_CONTACTS] permission.
     * Ensure the permission is granted before invoking this method.
     *
     * @return A list of [Contact] objects, each containing a contact's name, phone number, and
     * optional photo URI. Returns an empty list if no contacts are found or if there's an error.
     */
    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    override fun collect(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        var cursor: Cursor? = null

        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                SORT_ORDER
            )

            cursor?.use { // Use 'use' to ensure cursor is closed automatically
                val nameColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoUriColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                if (it.moveToFirst()) {
                    do {
                        val name = it.getStringOrNull(nameColumnIndex) ?: "" // Handle nulls directly
                        val phoneNumber = it.getStringOrNull(numberColumnIndex) ?: "" // Handle nulls directly
                        val photoUri = it.getStringOrNull(photoUriColumnIndex) // Already nullable

                        if (name.isNotBlank()) {
                            contacts.add(Contact(name, phoneNumber, photoUri))
                        }
                    } while (it.moveToNext())
                }
            }
        } catch (e: SecurityException) {
            // Handle specific permission exception
            Log.e(TAG, "Missing READ_CONTACTS permission", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error collecting contacts", e)
        }
        return contacts.distinctBy { it.name } // Ensure uniqueness based on contact name
    }

    companion object {
        private const val TAG = "ContactsCollector" // Add tag
        private val PROJECTION: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        private const val SORT_ORDER = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
    }
}

// Extension function to simplify cursor string retrieval
fun Cursor.getStringOrNull(columnIndex: Int): String? {
    return if (columnIndex != -1) getString(columnIndex) else null
}
