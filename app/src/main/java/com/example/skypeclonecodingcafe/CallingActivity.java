package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private String receiverUserId="", receiverUserImage ="",receiverUserName="";
    private String senderUserId="", senderUserImage ="",senderUserName="";

    private TextView nameContact;
    private ImageView ProfileImage,makeCallBtn,cancelCallBtn;
    private CustomProgressView customProgressView;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);


        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        //receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        //receiverUserName = getIntent().getExtras().get("profile_name").toString();

        userRef = FirebaseDatabase.getInstance()
                .getReference().child(getString(R.string.db_user));

        nameContact = findViewById(R.id.name_calling);
        ProfileImage = findViewById(R.id.profile_image_calling);
        makeCallBtn = findViewById(R.id.make_call);
        cancelCallBtn = findViewById(R.id.make_cancel_call);
        customProgressView = findViewById(R.id.progress_bar_load);

        getAndSetUserProfileInfo();




    }

    private void getAndSetUserProfileInfo() {


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(receiverUserId).exists()){

                    receiverUserImage = dataSnapshot.child(receiverUserId).child("image").getValue().toString();
                    receiverUserName = dataSnapshot.child(receiverUserId).child("name").getValue().toString();

                    nameContact.setText(receiverUserName);
                    My.setImage(customProgressView,receiverUserImage,ProfileImage);
                }

                if(dataSnapshot.child(senderUserId).exists()){

                    senderUserImage = dataSnapshot.child(senderUserId).child("image").getValue().toString();
                    senderUserName = dataSnapshot.child(senderUserId).child("name").getValue().toString();

                   // nameContact.setText(receiverUserName);
                    //My.setImage(customProgressView,receiverUserImage,ProfileImage);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing"))
                {
                    final HashMap<String,Object> callingInfo = new HashMap<>();
                  /*  callingInfo.put("uid",senderUserId);
                    callingInfo.put("name",senderUserName);
                    callingInfo.put("image",senderUserImage);*/
                    callingInfo.put("calling",receiverUserId);

                    userRef.child(senderUserId).child("Calling")
                            .updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        final HashMap<String,Object> ringingInfo = new HashMap<>();
                                       /* ringingInfo.put("uid",receiverUserId);
                                        ringingInfo.put("name",receiverUserName);
                                        ringingInfo.put("image",receiverUserImage);*/
                                        ringingInfo.put("ringing",senderUserId);

                                        userRef.child(receiverUserId).child("Ringing")
                                                .updateChildren(ringingInfo)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                        }
                                                    }
                                                });
                                    }
                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
