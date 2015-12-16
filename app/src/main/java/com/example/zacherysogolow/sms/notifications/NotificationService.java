package com.example.zacherysogolow.sms.notifications;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zacherysogolow.sms.data.SMSDbHelper;
import com.example.zacherysogolow.sms.domain.MySMSMessage;

import java.util.List;

/**
 * Handles background services and notifications.
 */
public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";

    public static final int PSEDUO_MSG_ID_NEW = -1007;

    public static final String ACTION_NEW_SMS = "com.example.zacherysogolow.sms.notifications.action.NEW";
    public static final String ACTION_SCHEDULE_NEW_SMS = "com.example.zacherysogolow.sms.notifications.action.SCHEDULE";
    public static final String ACTION_SNOOZE_SMS = "com.example.zacherysogolow.sms.notifications.action.SNOOZE";
    public static final String ACTION_SCHEDULED_SMS_DUE = "com.example.zacherysogolow.sms.notifications.action.DUE";
    public static final String ACTION_SMS_DISMISSED = "com.example.zacherysogolow.sms.notifications.action.DISMISS";

    public static final String EXTRA_WHO_FROM = "com.example.zacherysogolow.sms.notifications.extra.WHO_FROM";
    public static final String EXTRA_MSG_BODY = "com.example.zacherysogolow.sms.notifications.extra.MSG_BODY";
    public static final String EXTRA_ALARM_TIME = "com.example.zacherysogolow.sms.notifications.extra.ALARM_TIME";
    public static final String EXTRA_MSG_ID = "com.example.zacherysogolow.sms.notifications.extra.MSG_ID";
    public static final String EXTRA_MSG_THREAD_ID = "com.example.zacherysogolow.sms.notifications.extra.MSG_THREAD_ID";

    /**
     *
     */
    public NotificationService() {
        super("NotificationService");
    }

    /**
     *
     * @param context
     * @param from
     * @param body
     */
    public static void startActionNewSMS(Context context, String from, String body, long threadId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_NEW_SMS);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadId);
        context.startService(intent);
    }

    /**
     *
     * @param context
     * @param from
     * @param body
     * @param length
     */
    public static void startActionScheduleSMS(Context context, String from, String body, long length, long threadId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SCHEDULE_NEW_SMS);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_ALARM_TIME, length);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadId);
        context.startService(intent);
    }

    /**
     *
     * @param context
     * @param from
     * @param body
     * @param length
     */
    public static void startActionSnoozeSMS(Context context, String from, String body, long length, long threadId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SNOOZE_SMS);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_ALARM_TIME, length);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadId);
        context.startService(intent);
    }

    /**
     *
     * @param context
     * @param from
     * @param body
     */
    public static void startActionScheduledSMSDue(Context context, String from, String body, int msgId, long threadId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SCHEDULED_SMS_DUE);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_MSG_ID, msgId);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadId);
        context.startService(intent);
    }

    /**
     *
     * @param context
     * @param id
     */
    public static void startActionDismissSMS(Context context, int id) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SMS_DISMISSED);
        intent.putExtra(EXTRA_MSG_ID, id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEW_SMS.equals(action)) {
                final String from = intent.getStringExtra(EXTRA_WHO_FROM);
                final String body = intent.getStringExtra(EXTRA_MSG_BODY);
                final long threadId = intent.getLongExtra(EXTRA_MSG_THREAD_ID, 0);
                handleActionNewSMS(from, body, threadId);
            } else if (ACTION_SCHEDULE_NEW_SMS.equals(action)) {
                final String from = intent.getStringExtra(EXTRA_WHO_FROM);
                final String body = intent.getStringExtra(EXTRA_MSG_BODY);
                final long timeLength = intent.getLongExtra(EXTRA_ALARM_TIME, 0);
                final long threadId = intent.getLongExtra(EXTRA_MSG_THREAD_ID, 0);
                handleActionScheduleNewSMS(from, body, timeLength, threadId);
            } else if (ACTION_SNOOZE_SMS.equals(action)) {
                final String from = intent.getStringExtra(EXTRA_WHO_FROM);
                final String body = intent.getStringExtra(EXTRA_MSG_BODY);
                final long timeLength = intent.getLongExtra(EXTRA_ALARM_TIME, 0);
                final int msgId = intent.getIntExtra(EXTRA_MSG_ID, -1);
                final long threadId = intent.getLongExtra(EXTRA_MSG_THREAD_ID, 0);
                handleActionSnoozeSMS(from, body, timeLength, msgId, threadId);
            } else if (ACTION_SCHEDULED_SMS_DUE.equals(action)) {
                final String from = intent.getStringExtra(EXTRA_WHO_FROM);
                final String body = intent.getStringExtra(EXTRA_MSG_BODY);
                final int msgId = intent.getIntExtra(EXTRA_MSG_ID, -1);
                final long threadId = intent.getLongExtra(EXTRA_MSG_THREAD_ID, 0);
                handleActionScheduledSMSDue(from, body, msgId, threadId);
            } else if (ACTION_SMS_DISMISSED.equals(action)) {
                final int msgId = intent.getIntExtra(EXTRA_MSG_ID, -1);
                handleActionDismissSMS(msgId);
            }
        }
    }

    /**
     * Show notification
     * ask to schedule or dismiss
     * click intent go to message
     *
     * @param from
     * @param body
     */
    private void handleActionNewSMS(String from, String body, long threadId) {
        Log.d(TAG, "handleActionNewSMS()");

        Context context = getApplicationContext();

        MySMSMessage message = new MySMSMessage(-1, from, body, false, threadId);

        NotificationCompat.Builder mBuilder =
                NotificationFactory.getIncomingSMSNotificationBuilder(context,
                        android.R.drawable.btn_star,
                        message);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NotificationFactory.NOTIFY_NEW_SMS, mBuilder.build());
    }

    /**
     * Store message in database
     * start alarm
     *
     * @param from
     * @param body
     * @param timeLength
     */
    private void handleActionScheduleNewSMS(String from, String body, long timeLength, long threadId) {
        Log.d(TAG, "handleActionScheduleNewSMS()");

        Context context = getApplicationContext();

        // store in database
        // start alarm
        // dismiss event
        SMSDbHelper dbHelper = new SMSDbHelper(context);
        int rowId = (int)dbHelper.insertSMS(from, body, true, threadId);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SCHEDULED_SMS_DUE);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_MSG_ID, rowId);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadId);

        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        timeLength, alarmIntent);

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Service.NOTIFICATION_SERVICE);
        nm.cancel(NotificationFactory.NOTIFY_NEW_SMS);
    }

    /**
     *
     * @param from
     * @param body
     * @param timeLength
     */
    private void handleActionSnoozeSMS(String from, String body, long timeLength, int msgId, long threadID) {
        Log.d(TAG, "handleActionSnoozeSMS()");

        Context context = getApplicationContext();

        // start alarm
        // dismiss event
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SCHEDULED_SMS_DUE);
        intent.putExtra(EXTRA_WHO_FROM, from);
        intent.putExtra(EXTRA_MSG_BODY, body);
        intent.putExtra(EXTRA_MSG_ID, msgId);
        intent.putExtra(EXTRA_MSG_THREAD_ID, threadID);

        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        timeLength, alarmIntent);

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Service.NOTIFICATION_SERVICE);
        nm.cancel(NotificationFactory.NOTIFY_SCHEDULED_SMS);
    }

    /**
     * Show notification
     * ask to snooze or dismiss
     * click intent go to message
     *
     * @param from
     * @param body
     */
    private void handleActionScheduledSMSDue(String from, String body, int msgId, long threadId) {
        Log.d(TAG, "handleActionScheduledSMSDue()");

        Context context = getApplicationContext();

        MySMSMessage message = new MySMSMessage(msgId, from, body, false, threadId);

        NotificationCompat.Builder mBuilder =
                NotificationFactory.getAlarmNotificationBuilder(context,
                        android.R.drawable.btn_star,
                        message);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NotificationFactory.NOTIFY_SCHEDULED_SMS, mBuilder.build());
    }

    /**
     * Remove from database
     *
     * @param msgId
     */
    private void handleActionDismissSMS(int msgId) {
        Log.d(TAG, "handleActionDismissedSMS()");

        Context context = getApplicationContext();

        // cancel the notification because the id does not exists, this means the user
        // clicked the dismiss action from a new incoming message, not the notification
        // coming from a scheduled alarm
        if (msgId == PSEDUO_MSG_ID_NEW) {
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Service.NOTIFICATION_SERVICE);
            nm.cancel(NotificationFactory.NOTIFY_NEW_SMS);
        } else {
            // dismissed from scheduled notification, whether its a swipe or dismiss
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Service.NOTIFICATION_SERVICE);
            nm.cancel(NotificationFactory.NOTIFY_SCHEDULED_SMS);

            // delete from db
            SMSDbHelper helper = new SMSDbHelper(context);
            int result = helper.removeSMS(msgId);
        }
    }
}
