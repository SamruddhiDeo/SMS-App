package com.example.smsapp;

import static com.example.smsapp.MainActivity.dbHelper;

import android.telephony.SmsManager;
import android.util.Log;

import com.example.smsapp.ModelClasses.SmsModel;

public class SmsSender {
    public static void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
