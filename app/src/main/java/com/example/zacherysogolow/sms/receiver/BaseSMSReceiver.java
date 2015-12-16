package com.example.zacherysogolow.sms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Created by Zachery.Sogolow on 12/14/2015.
 */
public abstract class BaseSMSReceiver extends BroadcastReceiver {
    /**
     * Tag for identify class in Log
     */
    private static final String TAG = "SMSReceiver";

    /**
     * Broadcast action for received SMS
     */
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * Processes Intent data into SmsMessage array and calls onSmsReceived
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && (action.equals(ACTION_SMS_RECEIVED))) {

            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

            onSmsReceived(context, messages);
        }
    }

    protected abstract void onSmsReceived(Context context, SmsMessage[] messages);
}
