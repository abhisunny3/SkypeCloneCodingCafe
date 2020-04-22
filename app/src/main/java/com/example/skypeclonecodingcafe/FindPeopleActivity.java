package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindPeopleActivity extends AppCompatActivity {


    private RecyclerView findFriendsList;
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        searchET  = findViewById(R.id.search_user_edt);
        findFriendsList =findViewById(R.id.find_friends_list);

        findFriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        // test ss
    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTxt;
        Button videoCallBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        public FindFriendsViewHolder(@NonNull View itemview) {
            super(itemview);

            userNameTxt =itemview.findViewById(R.id.name_contacts);
            videoCallBtn =itemview.findViewById(R.id.call_btn);
            profileImageView =itemview.findViewById(R.id.imag_contacts);
            cardView =itemview.findViewById(R.id.card_view);
        }
    }
}
