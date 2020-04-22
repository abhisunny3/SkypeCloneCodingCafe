package com.example.skypeclonecodingcafe;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    private  BottomNavigationView navView;

    private RecyclerView myContactsList;
    private ImageView findPeopleBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

         navView = findViewById(R.id.nav_view);
         navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        findPeopleBtn = findViewById(R.id.find_people_btn);
        myContactsList = findViewById(R.id.contact_list);

        myContactsList.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));

        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentpeople = new Intent(ContactsActivity.this,FindPeopleActivity.class);
                startActivity(intentpeople);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            switch (menuItem.getItemId())
            {
                case R.id.navigation_home:
                    Intent intentMain = new Intent(ContactsActivity.this, ContactsActivity.class);
                    startActivity(intentMain);
                    break;
                case R.id.navigation_settings:
                    Intent intentsettings = new Intent(ContactsActivity.this,SettingsActivity.class);
                    startActivity(intentsettings);
                    break;

                case R.id.navigation_notifications:
                    Intent intentNotification = new Intent(ContactsActivity.this,NotificationsAcitivity.class);
                    startActivity(intentNotification);
                    break;

                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intentLogout= new Intent(ContactsActivity.this,RegistrationActivity.class);
                    startActivity(intentLogout);
                    finish();
                    break;


            }
            return true;
        }
    };

}
