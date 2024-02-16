package cu.suitetecsa.sdk.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cu.suitetecsa.sdk.android.model.Contact;

/**
 * Implementation of {@link ContactsCollector} for collecting contact information from the device.
 */
class ContactsCollectorImpl implements ContactsCollector {
    private final Context context;

    /**
     * Constructor for {@link ContactsCollectorImpl}.
     *
     * @param context the {@link Context} used to access the contacts.
     */
    ContactsCollectorImpl(Context context) {
        this.context = context;
    }

    /**
     * Collects contact information from the device.
     * <p>
     * Note: This method requires {@link android.Manifest.permission#READ_CONTACTS} permission.
     * Ensure the permission is granted before invoking this method.
     * </p>
     *
     * @return A list of {@link Contact} objects containing contact names and their associated phone numbers.
     */
    @Override
    public List<Contact> collect() {
        Map<String, Contact> contactsMap = new HashMap<>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.Contacts.PHOTO_URI
                },
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                @SuppressLint("Range") String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                if (!contactsMap.containsKey(name)) {
                    contactsMap.put(name, new Contact(name, phoneNumber, photoUri));
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        return new ArrayList<>(contactsMap.values());
    }
}
