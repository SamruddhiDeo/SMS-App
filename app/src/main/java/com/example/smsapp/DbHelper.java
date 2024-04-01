package com.example.smsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smsapp.ModelClasses.MessageModel;
import com.example.smsapp.ModelClasses.SmsModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmsDB";
    private static final int DATABASE_VERSION = 2;
    private static final String SENDER_TABLE_NAME = "sender";
    private static final String SENDER_COLUMN_ID = "id";
    private static final String SENDER_COLUMN_PHONE_NO = "sender_phone";
    private static final String SENDER_COLUMN_MESSAGE_BODY = "message_body";
    private static final String SENDER_COLUMN_DETECTED_URL = "url";
    private static final String SENDER_COLUMN_SENT_FLAG = "sent";
    private static final String SENDER_COLUMN_TIMESTAMP = "timestamp";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SENDER_TABLE_NAME + "("
                + SENDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SENDER_COLUMN_PHONE_NO + " TEXT , "
                + SENDER_COLUMN_MESSAGE_BODY + " TEXT , "
                + SENDER_COLUMN_DETECTED_URL + " TEXT , "
                + SENDER_COLUMN_SENT_FLAG + " TEXT , "
                + SENDER_COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + SENDER_TABLE_NAME + "'");
        onCreate(db);
    }

    public void storeSms(SmsModel smsModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SENDER_COLUMN_PHONE_NO, smsModel.getSenderPhone());
        values.put(SENDER_COLUMN_MESSAGE_BODY, smsModel.getMessageBody());
        values.put(SENDER_COLUMN_DETECTED_URL, smsModel.getUrl());
        values.put(SENDER_COLUMN_SENT_FLAG, smsModel.getSentFlag());

        db.insert(SENDER_TABLE_NAME, null, values);
    }

    public ArrayList<SmsModel> fetchSender() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SmsModel> phoneNumbers = new ArrayList<>();

        String query = "SELECT DISTINCT " + SENDER_COLUMN_PHONE_NO +
                " FROM " + SENDER_TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(SENDER_COLUMN_PHONE_NO));

                phoneNumbers.add(new SmsModel(phoneNumber, "messsage", "There is no url in this message", "0"));
            }
        }
        return phoneNumbers;
    }

    public ArrayList<MessageModel> fetchMessages(String phoneNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MessageModel> messages = new ArrayList<>();

        String query = "SELECT " + SENDER_COLUMN_MESSAGE_BODY + ", " + SENDER_COLUMN_SENT_FLAG + ", " + SENDER_COLUMN_DETECTED_URL + " FROM " + SENDER_TABLE_NAME + " WHERE " + SENDER_COLUMN_PHONE_NO + " = '" + phoneNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(SENDER_COLUMN_MESSAGE_BODY));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(SENDER_COLUMN_DETECTED_URL));
                String sentFlag = cursor.getString(cursor.getColumnIndexOrThrow(SENDER_COLUMN_SENT_FLAG));
//                Log.d("checkbug",message+"   "+url);
                messages.add(new MessageModel(message, url, sentFlag));
            }
        }
        return messages;
    }

    public String fetchLatestMessage(String phoneNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String latestMessage = "";
        String query = "SELECT " + SENDER_COLUMN_MESSAGE_BODY + " FROM " + SENDER_TABLE_NAME + " WHERE " + SENDER_COLUMN_PHONE_NO + " = '" + phoneNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                latestMessage = cursor.getString(cursor.getColumnIndexOrThrow(SENDER_COLUMN_MESSAGE_BODY));
            }
        }
        return latestMessage;
    }
}