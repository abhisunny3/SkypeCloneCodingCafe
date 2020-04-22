package com.example.skypeclonecodingcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText userNameET,userBioET;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveBtn = findViewById(R.id.save_setting_btn);
        userNameET = findViewById(R.id.username_setting);
        userBioET = findViewById(R.id.bio_setting);
        profileImageView = findViewById(R.id.setting_profile_image);

    }
}
