package com.example.skypeclonecodingcafe;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    private  BottomNavigationView navView;

    private RecyclerView myContactsList;
    private ImageView findPeopleBtn;

    private FirebaseAuth mAuth;
    private String currentUserId = "";
    private DatabaseReference contactsRef,usersRef;
    private String userName,profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_contacts));
        usersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_user));


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

    @Override
    protected void onStart() {
        super.onStart();

        validateUser();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactListViewHolder>  firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, ContactListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactListViewHolder holder, int position, @NonNull Contacts model) {

                String   listUserId = getRef(position).getKey();

                usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            userName =dataSnapshot.child("name").getValue().toString();
                            profileImage =dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(profileImage).into(holder.profileImageView);
                            holder.userNameTxt.setText(userName);
                        }

                        holder.callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ContactsActivity.this,CallingActivity.class);
                                intent.putExtra("visit_user_id",listUserId);
                                intent.putExtra("profile_image",model.getImage());
                                intent.putExtra("profile_name",model.getName());
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                ContactListViewHolder viewHolder = new ContactListViewHolder(view);

                return viewHolder;
            }
        };

        myContactsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void validateUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child(getString(R.string.db_user));


        reference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    Intent intent = new Intent(ContactsActivity.this,SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public static class ContactListViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTxt;
        Button callBtn;
        ImageView profileImageView;

        public ContactListViewHolder(@NonNull View itemview) {
            super(itemview);

            userNameTxt =itemview.findViewById(R.id.name_contacts);
            callBtn =itemview.findViewById(R.id.call_btn);
            profileImageView =itemview.findViewById(R.id.imag_contacts);
        }
    }

}
