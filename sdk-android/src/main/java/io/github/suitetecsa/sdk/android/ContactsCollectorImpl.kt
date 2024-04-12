package io.github.suitetecsa.sdk.android

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import io.github.suitetecsa.sdk.android.model.Contact

/**
 * Implementation of [ContactsCollector] for collecting contact information from the device.
 */
internal class ContactsCollectorImpl
/**
 * Constructor for [ContactsCollectorImpl].
 *
 * @param context the [Context] used to access the contacts.
 */(private val context: Context) : ContactsCollector {
    /**
     * Collects contact information from the device.
     *
     *
     * Note: This method requires [android.Manifest.permission.READ_CONTACTS] permission.
     * Ensure the permission is granted before invoking this method.
     *
     *
     * @return A list of [Contact] objects containing contact names and their associated phone numbers.
     */
    override fun collect(): List<Contact> {
        val contactsMap: MutableMap<String, Contact> = HashMap()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.PHOTO_URI
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                @SuppressLint("Range") val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                @SuppressLint("Range") val photoUri =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                if (!contactsMap.containsKey(name)) {
                    contactsMap[name] = Contact(name, phoneNumber, photoUri)
                }
                cursor.moveToNext()
            }
            cursor.close()
        }
        return ArrayList(contactsMap.values)
    }
}
