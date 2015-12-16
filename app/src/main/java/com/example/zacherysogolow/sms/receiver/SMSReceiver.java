package com.example.zacherysogolow.sms.receiver;

import android.content.Context;
import android.telephony.SmsMessage;

import com.example.zacherysogolow.sms.notifications.NotificationService;

/**
 * Created by Zachery.Sogolow on 12/10/2015.
 */
public class SMSReceiver extends BaseSMSReceiver {
    public static final String TAG = "SMSReceiver";

    @Override
    protected void onSmsReceived(Context context, SmsMessage[] messages) {

        NotificationService.startActionNewSMS(context, messages[0].getOriginatingAddress(),
                messages[0].getMessageBody());
    }
}

