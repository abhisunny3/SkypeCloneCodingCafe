package com.example.skypeclonecodingcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId="", receiverUserImage ="",receiverUserName="";
    private ImageView backgroundProfileImage;
    private TextView name_profile;
    private Button add_friend,decline_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();

        backgroundProfileImage  =findViewById(R.id.background_profile_image);
        name_profile  =findViewById(R.id.name_profile);
        add_friend  =findViewById(R.id.add_friends);
        decline_friend  =findViewById(R.id.decline_friends);

        Picasso.get().load(receiverUserImage).into(backgroundProfileImage);
        name_profile.setText(receiverUserName);

    }
}
