package com.example.smsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smsapp.DbHelper;
import com.example.smsapp.MessagesActivity;
import com.example.smsapp.ModelClasses.SmsModel;
import com.example.smsapp.R;

import java.util.ArrayList;

public class ViewMessageAdapter extends RecyclerView.Adapter<ViewMessageAdapter.ViewHolder> {
    Context context;
    ArrayList<SmsModel> arrViewMessages;
    DbHelper dbHelper;

    public ViewMessageAdapter(Context context, ArrayList<SmsModel> arrViewMessages) {
        this.arrViewMessages = arrViewMessages;
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_message_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMessageAdapter.ViewHolder holder, int position) {
        holder.senderName.setText(arrViewMessages.get(position).getSenderPhone());
        holder.messageTxt.setText(dbHelper.fetchLatestMessage(arrViewMessages.get(position).getSenderPhone()));
        holder.viewMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMessages = new Intent(context, MessagesActivity.class);
                iMessages.putExtra("phoneNo", arrViewMessages.get(holder.getAdapterPosition()).getSenderPhone());
                context.startActivity(iMessages);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrViewMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageTxt;
        ConstraintLayout viewMessageLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageTxt = itemView.findViewById(R.id.messageTxt);
            viewMessageLayout = itemView.findViewById(R.id.viewMessageLayout);
        }
    }
}
