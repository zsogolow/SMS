package com.example.zacherysogolow.sms.domain;

/**
 * Created by Zachery.Sogolow on 12/15/2015.
 */
public class MySMSMessage {
    private int mId;
    private String mFrom;
    private String mBody;
    private boolean mIsScheduled;

    public MySMSMessage(int id, String from, String body, boolean isScheduled) {
        mId = id;
        mFrom = from;
        mBody = body;
        mIsScheduled = isScheduled;
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
}
