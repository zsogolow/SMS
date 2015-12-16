package com.example.zacherysogolow.sms.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

/**
 * Created by Zachery.Sogolow on 12/16/2015.
 */
public class MySMSUtil {
    public static final Uri MMS_SMS_CONTENT_URI = Telephony.MmsSms.CONTENT_URI;
    public static final Uri THREAD_ID_CONTENT_URI =
            Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "threadID");

    public static long findThreadIdFromAddress(Context context, String address) {
        if (address == null)
            return 0;

        String THREAD_RECIPIENT_QUERY = "recipient";

        Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
        uriBuilder.appendQueryParameter(THREAD_RECIPIENT_QUERY, address);

        long threadId = 0;

        Cursor cursor = null;
        try {

            cursor = context.getContentResolver().query(
                    uriBuilder.build(),
                    new String[] { ContactsContract.Contacts._ID },
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                threadId = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return threadId;
    }

}
