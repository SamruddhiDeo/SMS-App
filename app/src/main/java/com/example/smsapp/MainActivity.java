package com.example.smsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsapp.Adapters.ViewMessageAdapter;
import com.example.smsapp.ModelClasses.ApiPojoModel;
import com.example.smsapp.ModelClasses.SmsModel;
import com.example.smsapp.ModelClasses.MessageModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    static DbHelper dbHelper;
    static RecyclerView viewMessageRecyclerView;
    static ArrayList<SmsModel> arrSender;
    static LinearLayoutManager linearLayoutManager;
    static ViewMessageAdapter viewMessageAdapter;
    EditText sendToEditTxt;
    String sendToPhone;
    String sendMsg;
    private ActivityResultLauncher<Intent> contactLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permissions
        String permissionSms = Manifest.permission.READ_SMS;
        String permissionContacts = Manifest.permission.READ_CONTACTS;
        if (ContextCompat.checkSelfPermission(this, permissionSms)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permissionContacts) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.showPermissionDialog(this);
        }

        dbHelper = new DbHelper(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewMessageRecyclerView = findViewById(R.id.viewMessageRecyclerView);
        ImageButton sendSmsBtn = findViewById(R.id.sendSmsBtn);
        ImageButton selectContact = findViewById(R.id.selectContact);
        sendToEditTxt = findViewById(R.id.sendToEditTxt);
        EditText sendMsgEditTxt = findViewById(R.id.sendMsgEditTxt);

        setSupportActionBar(toolbar);

        notifyChangeToMainActivityRecyclerView();

        //send button
        sendSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPhone = sendToEditTxt.getText().toString().trim();
                sendMsg = sendMsgEditTxt.getText().toString();

                if (sendToPhone.matches("^\\+\\d{2}\\d{10}$")) {
                    SmsSender.sendSms(sendToPhone, sendMsg);
                    sendToEditTxt.setText("");
                    sendMsgEditTxt.setText("");
                    String url = detectUrl(sendMsg);
                    if (url != null) {
                        scanUrl(url);
                    } else {
                        dbHelper.storeSms(new SmsModel(sendToPhone, sendMsg, "There is no url in this message", "1"));
                        notifyChangeToMainActivityRecyclerView();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize ActivityResultLauncher
        contactLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the selected contact
                        String contactNumber = getContactNumber(data);
                        contactNumber = contactNumber.replaceAll("\\s+", "");
                        if (contactNumber.length() == 10) {
                            contactNumber = "+91" + contactNumber;
                        }
                        sendToEditTxt.setText(contactNumber);
                    }
                });

        //select contacts from phone
        selectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                contactLauncher.launch(intent);
            }
        });
    }


    //to refresh messages
    public static void notifyChangeToMainActivityRecyclerView() {
        arrSender = dbHelper.fetchSender();
        viewMessageRecyclerView.setLayoutManager(new LinearLayoutManager(viewMessageRecyclerView.getContext()));
        viewMessageAdapter = new ViewMessageAdapter(viewMessageRecyclerView.getContext(), arrSender);
        viewMessageRecyclerView.setAdapter(viewMessageAdapter);
    }

    public static void notifyViewMessages() {
        viewMessageRecyclerView.setAdapter(viewMessageAdapter);
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
                        dbHelper.storeSms(new SmsModel(sendToPhone, sendMsg, safety, "1"));
                        notifyChangeToMainActivityRecyclerView();
                        safety = "";

                    } else {
//                        Log.d("checkbug", "Response is null");
                    }
                } else {
//                    Log.d("checkbug", "Response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<ApiPojoModel> call, Throwable throwable) {
//                Log.d("checkbug", "fail");
            }
        });
    }

    //find if message has url
    private String detectUrl(String messageBody) {
        Pattern pattern = Pattern.compile("(?i)\\b(https?://)?(www\\.[a-z0-9-]+(\\.[a-z]{2,})+\\S*)");
        Matcher matcher = pattern.matcher(messageBody);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String getContactNumber(Intent data) {
        String contactNumber = null;
        if (data != null) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contactUri.getLastPathSegment())},
                    null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        contactNumber = cursor.getString(numberIndex);
                    } else {
                        Toast.makeText(this, "No phone number found for the selected contact", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve contact information", Toast.LENGTH_SHORT).show();
            }
        }
        return contactNumber;
    }
}