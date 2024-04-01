package com.example.smsapp;

import static android.Manifest.permission_group.CALENDAR;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.smsapp.ModelClasses.ApiPojoModel;
import com.example.smsapp.ModelClasses.SmsModel;

import java.util.Calendar;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsReceiver extends BroadcastReceiver {
    private Bundle bundle;
    private SmsMessage smsMessage;
    public String sender;
    public String messageBody;
    DbHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DbHelper(context);

        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                        sender = smsMessage.getDisplayOriginatingAddress();
                        messageBody = smsMessage.getDisplayMessageBody();

                        String url = detectUrl(messageBody);
                        if (url != null) {
                            scanUrl(url);
                        } else {
                            dbHelper.storeSms(new SmsModel(sender, messageBody, "There is no url in this message", "0"));
                        }
                    }
                }
            }
        }
        MainActivity.notifyChangeToMainActivityRecyclerView();
        MainActivity.notifyViewMessages();
    }

    String safety = "";
    public void scanUrl(String url) {
        IpQualityScoreApi ipQualityScoreApi = RetrofitInstance.getRetrofit().create(IpQualityScoreApi.class);
        Call<ApiPojoModel> call = ipQualityScoreApi.checkUrl(url);
        call.enqueue(new Callback<ApiPojoModel>() {
            @Override
            public void onResponse(Call<ApiPojoModel> call, Response<ApiPojoModel> response) {
                if (response.isSuccessful()) {
                    ApiPojoModel urlResponse = response.body();
                    if (urlResponse != null) {
                        int riskScore = Integer.parseInt(urlResponse.getRisk_score());
                        boolean phishing = Boolean.parseBoolean(urlResponse.getPhishing());
                        boolean malware = Boolean.parseBoolean(urlResponse.getMalware());
                        boolean suspicious = Boolean.parseBoolean(urlResponse.getMalware());

                        if (riskScore == 100 && phishing == true && malware == true) {
                            safety = "Risk score of " + url + " is equal to 100 phishing or malware(indicates confirmed malware or phishing activity in the past 24-48 hours)";
                        } else if (riskScore >= 90) {
                            safety = "Risk score of " + url + " is greater than 90 and high risk(strong confidence the URL is malicious)";
                        } else if (riskScore >= 75) {
                            safety = "Risk score of " + url + " is greater than 75, suspicious(usually due to patterns associated with malicious links)";
                        } else if (suspicious == true) {
                            safety = "The " + url + " have a high chance for being involved in abusive behavior.";
                        } else {
                            safety = "Url " + url + " is safe to open";
                        }
                        dbHelper.storeSms(new SmsModel(sender, messageBody, safety, "0"));
                        safety = "";
                    } else {
//                        Log.d("checkbug", "Response is null");
                    }
                } else {
//                        Log.d("checkbug", "Response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<ApiPojoModel> call, Throwable throwable) {
                Log.d("checkbug", "Response failed");
            }
        });

    }

    private String detectUrl(String messageBody) {
        Pattern pattern = Pattern.compile("(?i)\\b(https?://)?(www\\.[a-z0-9-]+(\\.[a-z]{2,})+\\S*)");
        Matcher matcher = pattern.matcher(messageBody);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
