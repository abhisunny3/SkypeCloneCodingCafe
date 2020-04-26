package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationsAcitivity extends AppCompatActivity {

    private RecyclerView notificationList;

    private FirebaseAuth mAuth;
    private String currentUserId = "";
    private DatabaseReference friendsRequestRef,contactsRef,usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_acitivity);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        friendsRequestRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_friends_request));
        contactsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_contacts));
        usersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_user));


        notificationList = findViewById(R.id.notification_list);
        notificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = null;

        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(friendsRequestRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Contacts model) {


             String   listUserId = getRef(position).getKey();




                holder.acceptBtn.setVisibility(View.VISIBLE);
                holder.cancelBtn.setVisibility(View.VISIBLE);

                DatabaseReference requestTypeRef = getRef(position).child("request_type").getRef();

                requestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();

                            if(type.equals("received")){
                                holder.cardView.setVisibility(View.VISIBLE);

                                usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image")){
                                            final String imagstr = dataSnapshot.child("image").getValue().toString();
                                            final String namestr = dataSnapshot.child("name").getValue().toString();

                                            Picasso.get().load(imagstr).into(holder.profileImageView);
                                        }
                                        final String namestr = dataSnapshot.child("name").getValue().toString();
                                        holder.userNameTxt.setText(namestr);

                                        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AcceptFriendsRequest(listUserId);
                                            }
                                        });

                                        holder.cancelBtn.setOnClickListener(view -> cancelFriendRequest(listUserId));

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                holder.cardView.setVisibility(View.GONE);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_design,parent,false);
                NotificationViewHolder viewHolder = new NotificationViewHolder(view);

                return viewHolder;
            }
        };

        notificationList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

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

    private void AcceptFriendsRequest(String listUserId) {
        contactsRef.child(currentUserId).child(listUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    contactsRef.child(listUserId).child(currentUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                friendsRequestRef.child(currentUserId).child(listUserId).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    friendsRequestRef.child(listUserId).child(currentUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(NotificationsAcitivity.this, "New contact saved succesfully", Toast.LENGTH_SHORT).show();
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

    private void cancelFriendRequest(String listUserId) {
        friendsRequestRef.child(currentUserId).child(listUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            friendsRequestRef.child(currentUserId).child(listUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(NotificationsAcitivity.this, "friend request cancelled", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
