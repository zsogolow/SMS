package com.example.zacherysogolow.sms.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.example.zacherysogolow.sms.R;
import com.example.zacherysogolow.sms.domain.MySMSMessage;

/**
 * Created by Zachery.Sogolow on 12/14/2015.
 */
public class NotificationFactory {
    private static final String TAG = "NotificationFactory";

    public static final int NOTIFY_NEW_SMS = 1007;
    public static final int NOTIFY_SCHEDULED_SMS = 2007;

    public static NotificationCompat.Builder getIncomingSMSNotificationBuilder(Context context,
                                                                               int icon,
                                                                               MySMSMessage message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(message.getFrom())
                .setContentText(message.getBody());

        NotificationCompat.BigTextStyle bigTextStyle =
                new NotificationCompat.BigTextStyle();

        bigTextStyle.setBigContentTitle(message.getFrom());
        bigTextStyle.bigText(message.getBody());
        mBuilder.setStyle(bigTextStyle);

        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        // Intents
        Intent scheduleIntent = new Intent(context, NotificationService.class);
        scheduleIntent.setAction(NotificationService.ACTION_SCHEDULE_NEW_SMS);
        scheduleIntent.putExtra(NotificationService.EXTRA_WHO_FROM, message.getFrom());
        scheduleIntent.putExtra(NotificationService.EXTRA_MSG_BODY, message.getBody());
        scheduleIntent.putExtra(NotificationService.EXTRA_ALARM_TIME, 10000l);

        Intent dismissIntent = new Intent(context, NotificationService.class);
        dismissIntent.setAction(NotificationService.ACTION_SMS_DISMISSED);
        dismissIntent.putExtra(NotificationService.EXTRA_MSG_ID, NotificationService.PSEDUO_MSG_ID_NEW);

        // Pending Intents
        PendingIntent schedulePendingIntent =
                PendingIntent.getService(context, (int) System.currentTimeMillis(), scheduleIntent, 0);
        PendingIntent dismissPendingIntent =
                PendingIntent.getService(context, (int) System.currentTimeMillis(), dismissIntent, 0);

        mBuilder.addAction(R.drawable.schedule, "schedule", schedulePendingIntent);
        mBuilder.addAction(R.drawable.dismiss, "dismiss", dismissPendingIntent);

        mBuilder.setDeleteIntent(dismissPendingIntent);
        return mBuilder;
    }

    public static NotificationCompat.Builder getAlarmNotificationBuilder(Context context,
                                                                         int icon,
                                                                         MySMSMessage message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(message.getFrom())
                .setContentText(message.getBody());

        NotificationCompat.BigTextStyle bigTextStyle =
                new NotificationCompat.BigTextStyle();

        bigTextStyle.setBigContentTitle(message.getFrom());
        bigTextStyle.bigText(message.getBody());
        mBuilder.setStyle(bigTextStyle);

        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        // Intents
        Intent snoozeIntent = new Intent(context, NotificationService.class);
        snoozeIntent.setAction(NotificationService.ACTION_SNOOZE_SMS);
        snoozeIntent.putExtra(NotificationService.EXTRA_WHO_FROM, message.getFrom());
        snoozeIntent.putExtra(NotificationService.EXTRA_MSG_BODY, message.getBody());
        snoozeIntent.putExtra(NotificationService.EXTRA_ALARM_TIME, 10000l);
        snoozeIntent.putExtra(NotificationService.EXTRA_MSG_ID, message.getId());

        Intent dismissIntent = new Intent(context, NotificationService.class);
        dismissIntent.setAction(NotificationService.ACTION_SMS_DISMISSED);
        dismissIntent.putExtra(NotificationService.EXTRA_MSG_ID, message.getId());

        // Pending Intents
        PendingIntent schedulePendingIntent =
                PendingIntent.getService(context, (int) System.currentTimeMillis(), snoozeIntent, 0);
        PendingIntent dismissPendingIntent =
                PendingIntent.getService(context, (int) System.currentTimeMillis(), dismissIntent, 0);

        mBuilder.addAction(R.drawable.schedule, "snooze", schedulePendingIntent);
        mBuilder.addAction(R.drawable.dismiss, "dismiss", dismissPendingIntent);

        mBuilder.setDeleteIntent(dismissPendingIntent);
        return mBuilder;
    }
}


