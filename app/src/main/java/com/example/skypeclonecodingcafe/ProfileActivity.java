package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId="", receiverUserImage ="",receiverUserName="";
    private ImageView backgroundProfileImage;
    private TextView name_profile;
    private Button add_friend,decline_friend;

    private FirebaseAuth mAuth;
    private String senderUserId = "",currentState ="new";
    private DatabaseReference friendsRequestRef,contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        friendsRequestRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_friends_request));
        contactsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_contacts));

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();

        backgroundProfileImage  =findViewById(R.id.background_profile_image);
        name_profile  =findViewById(R.id.name_profile);
        add_friend  =findViewById(R.id.add_friends);
        decline_friend  =findViewById(R.id.decline_friends);

        Picasso.get().load(receiverUserImage).into(backgroundProfileImage);
        name_profile.setText(receiverUserName);

        manageClickEvent();

    }

    private void manageClickEvent() {

        friendsRequestRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receiverUserId)){

                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                    Toast.makeText(ProfileActivity.this, ""+request_type, Toast.LENGTH_SHORT).show();

                    if(request_type.equals("sent")){
                        currentState="request_sent";
                        add_friend.setText("Cancel Friend Request");
                    }
                    else if(request_type.equals("received")){
                        currentState="request_received";
                        add_friend.setText("Accept Friend Request");

                        decline_friend.setVisibility(View.VISIBLE);
                        decline_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelFriendRequest();
                            }
                        });
                    }
                }
                else {

                    contactsRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(receiverUserId))
                            {
                                currentState ="friends";
                                add_friend.setText("Delete Contact");
                            }
                            else {
                                currentState = "new";
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(senderUserId.equals(receiverUserId)){
            add_friend.setVisibility(View.GONE);
        }
        else {
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(currentState.equals("new")){
                        SendFriendRequest();
                    }
                     if(currentState.equals("request_sent")){
                         cancelFriendRequest();
                    }
                     if(currentState.equals("request_received")){
                         AcceptFriendsRequest();
                    }
                     if(currentState.equals("request_sent")){
                         cancelFriendRequest();
                    }
                }
            });
        }

    }

    private void AcceptFriendsRequest() {
        contactsRef.child(senderUserId).child(receiverUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    contactsRef.child(receiverUserId).child(senderUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                friendsRequestRef.child(senderUserId).child(receiverUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    friendsRequestRef.child(receiverUserId).child(senderUserId).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){
                                                                        currentState ="friends";
                                                                        add_friend.setText("Delete Contact");
                                                                        decline_friend.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private void cancelFriendRequest() {
        friendsRequestRef.child(senderUserId).child(receiverUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            friendsRequestRef.child(receiverUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                currentState ="new";
                                                add_friend.setText("Add Friend");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest() {

        friendsRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    friendsRequestRef.child(receiverUserId).child(senderUserId)
                            .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                currentState="request_sent";
                                add_friend.setText("Cancel Friend Request");

                                Toast.makeText(ProfileActivity.this, "friends request sent successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

    }
}
