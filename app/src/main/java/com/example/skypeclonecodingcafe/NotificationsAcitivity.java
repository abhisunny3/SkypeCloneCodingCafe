package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationsAcitivity extends AppCompatActivity {


    private RecyclerView notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_acitivity);

        notificationList = findViewById(R.id.notification_list);
        notificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTxt;
        Button acceptBtn,cancelBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        public NotificationViewHolder(@NonNull View itemview) {
            super(itemview);

            userNameTxt =itemview.findViewById(R.id.name_notification);
            acceptBtn =itemview.findViewById(R.id.request_accept_btn);
            cancelBtn =itemview.findViewById(R.id.request_decline_btn);
            profileImageView =itemview.findViewById(R.id.imag_notification);
            cardView =itemview.findViewById(R.id.card_view);
        }
    }
}
