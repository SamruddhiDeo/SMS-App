package com.example.smsapp.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smsapp.ModelClasses.MessageModel;
import com.example.smsapp.ModelClasses.SmsModel;
import com.example.smsapp.R;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    ArrayList<MessageModel> arrMessages;

    public MessagesAdapter(Context context, ArrayList<MessageModel> arrMessages) {
        this.arrMessages = arrMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messages_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        holder.allMessagesTxt.setText(arrMessages.get(position).getMessage());
        if (arrMessages.get(position).getUrl().equals("There is no url in this message")) {
            holder.allMessagesUrlSafety.setVisibility(View.GONE);
        } else {
            holder.allMessagesUrlSafety.setText(arrMessages.get(position).getUrl());
        }
        if (arrMessages.get(position).getSent().equals("1")) {
            holder.msgLayout.setGravity(Gravity.RIGHT | Gravity.END);
            holder.msgLayout2.setGravity(Gravity.RIGHT | Gravity.END);
        } else {
            holder.msgLayout.setGravity(Gravity.LEFT | Gravity.START);
            holder.msgLayout2.setGravity(Gravity.LEFT | Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return arrMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView allMessagesTxt, allMessagesUrlSafety;
        LinearLayout msgLayout, msgLayout2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            allMessagesTxt = itemView.findViewById(R.id.allMessagesTxt);
            allMessagesUrlSafety = itemView.findViewById(R.id.allMessagesUrlSafety);
            msgLayout = itemView.findViewById(R.id.msgLayout);
            msgLayout2 = itemView.findViewById(R.id.msgLayout2);
        }
    }
}
