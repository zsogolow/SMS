package com.example.zacherysogolow.sms.domain;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

/**
 * Created by Zachery.Sogolow on 12/15/2015.
 */
public class MySMSMessage {

    private int mId;
    private String mFrom;
    private String mBody;
    private boolean mIsScheduled;
    private long mThreadId;

    public MySMSMessage(int id, String from, String body, boolean isScheduled, long threadId) {
        mId = id;
        mFrom = from;
        mBody = body;
        mIsScheduled = isScheduled;
        mThreadId = threadId;
    }

    public String getFrom() {
        return mFrom;
    }

    public String getBody() {
        return mBody;
    }

    public int getId() {
        return mId;
    }

    public long getThreadId() {
        return mThreadId;
    }
}
