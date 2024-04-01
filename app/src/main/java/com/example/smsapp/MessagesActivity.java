package com.example.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.smsapp.Adapters.MessagesAdapter;
import com.example.smsapp.Adapters.ViewMessageAdapter;
import com.example.smsapp.ModelClasses.MessageModel;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {
    static DbHelper dbHelper;
    static RecyclerView messagesRecyclerView;
    public static ArrayList<MessageModel> arrMessages;
    static LinearLayoutManager linearLayoutManager;
    static MessagesAdapter messagesAdapter;
    static String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        dbHelper = new DbHelper(this);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        //get phone number to fetch its messages
        Intent intent = getIntent();
        phoneNo = getIntent().getStringExtra("phoneNo");

        notifyChangeToMessagesRecyclerView();
    }

    //to refresh message from new contact
    public static void notifyChangeToMessagesRecyclerView() {
        arrMessages = dbHelper.fetchMessages(phoneNo);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(messagesRecyclerView.getContext()));
        messagesAdapter = new MessagesAdapter(messagesRecyclerView.getContext(), arrMessages);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }
    public static void notifyMessages() {
        messagesRecyclerView.setAdapter(messagesAdapter);
    }
}