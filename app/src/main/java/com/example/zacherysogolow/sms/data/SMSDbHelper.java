package com.example.zacherysogolow.sms.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zacherysogolow.sms.domain.MySMSMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachery.Sogolow on 12/14/2015.
 *
 */
public class SMSDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SMS.db";
    public static final String SMS_TABLE_NAME = "messages";
    public static final String SMS_COLUMN_ID = "id";
    public static final String SMS_COLUMN_FROM = "whoFrom";
    public static final String SMS_COLUMN_MESSAGE_BODY = "body";
    public static final String SMS_COLUMN_IS_SCHEDULED = "isScheduled";
    private static final String SMS_COLUMN_THREAD_ID = "threadId";

    public SMSDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER)",
                                SMS_TABLE_NAME, SMS_COLUMN_ID, SMS_COLUMN_FROM, SMS_COLUMN_MESSAGE_BODY, SMS_COLUMN_IS_SCHEDULED, SMS_COLUMN_THREAD_ID);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String upgrade = String.format("DROP TABLE IF EXISTS %s", SMS_TABLE_NAME);
        db.execSQL(upgrade);
        onCreate(db);
    }

    public long insertSMS(String from, String body, boolean isScheduled, long threadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMS_COLUMN_FROM, from);
        contentValues.put(SMS_COLUMN_MESSAGE_BODY, body);
        contentValues.put(SMS_COLUMN_IS_SCHEDULED, isScheduled ? 1 : 0);
        contentValues.put(SMS_COLUMN_THREAD_ID, threadId);
        return db.insert(SMS_TABLE_NAME, null, contentValues);
    }

    public int removeSMS(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SMS_TABLE_NAME, "id = ? ", new String[]{ Integer.toString(id) });
    }

    public boolean updateSMS(int id, String from, String body, boolean isScheduled, long threadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMS_COLUMN_FROM, from);
        contentValues.put(SMS_COLUMN_MESSAGE_BODY, body);
        contentValues.put(SMS_COLUMN_IS_SCHEDULED, isScheduled ? 1 : 0);
        contentValues.put(SMS_COLUMN_THREAD_ID, threadId);
        db.update(SMS_TABLE_NAME, contentValues, "id = ? ", new String[]{ Integer.toString(id) });
        return true;
    }

    public MySMSMessage getMessage(int id) {
        MySMSMessage mySMSMessage;
        SQLiteDatabase db = this.getReadableDatabase();
        String select = String.format("SELECT * FROM %s WHERE %s=%s", SMS_TABLE_NAME, SMS_COLUMN_ID, id);
        Cursor res =  db.rawQuery(select, null);
        res.moveToFirst();
        mySMSMessage = new MySMSMessage(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3) == 1, res.getLong(4));
        res.close();
        return mySMSMessage;
    }

    public List<MySMSMessage> getAllMessages() {
        List<MySMSMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = String.format("SELECT * FROM %s", SMS_TABLE_NAME);
        Cursor res =  db.rawQuery(select, null);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            int id = res.getInt(0);
            String from = res.getString(1);
            String body = res.getString(2);
            boolean isScheduled = res.getInt(3) == 1;
            long threadId = res.getLong(4);
            messages.add(new MySMSMessage(id, from, body, isScheduled, threadId));
            res.moveToNext();
        }
        res.close();
        return messages;
    }
}
