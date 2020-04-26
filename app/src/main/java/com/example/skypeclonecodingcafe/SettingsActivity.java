package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText userNameET,userBioET;
    private ImageView profileImageView;
    private int GALLERYPicker= 1;
    private Uri ImageUri;
    private StorageReference userProfileImgRef;
    private String donwloadUrl;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;
    private CustomProgressView progress_bar_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        userProfileImgRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.db_profile_image));
        userRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_user));

        saveBtn = findViewById(R.id.save_setting_btn);
        userNameET = findViewById(R.id.username_setting);
        userBioET = findViewById(R.id.bio_setting);
        profileImageView = findViewById(R.id.setting_profile_image);

        progress_bar_load= findViewById(R.id.progress_bar_load);

        progressDialog = new ProgressDialog(SettingsActivity.this);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERYPicker);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveUserData();
            }
        });

        retriveUserInfo();

    }

    private void saveUserData() {
        final String getUserName = userNameET.getText().toString();
        final String getBio = userBioET.getText().toString();

        if(ImageUri ==null){

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")){
                        SaveInfoOnly();
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "Please select image first.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(getUserName.equals("")) {
            Toast.makeText(this, "userName is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(getBio.equals("")) {
            Toast.makeText(this, "bio is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else {

            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("please wait");
            progressDialog.show();

            final StorageReference filePath = userProfileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask =filePath.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    donwloadUrl =filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        donwloadUrl =task.getResult().toString();

                        HashMap<String,Object> fileMap = new HashMap<>();
                        fileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        fileMap.put("name",getUserName);
                        fileMap.put("status",getBio);
                        fileMap.put("image",donwloadUrl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(fileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SettingsActivity.this,ContactsActivity.class);
                                    startActivity(intent);
                                    finish();

                                    Toast.makeText(SettingsActivity.this, "Profile Settings has been updated.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }

                }
            });
        }


    }

    private void SaveInfoOnly() {
        final String getUserName = userNameET.getText().toString();
        final String getBio = userBioET.getText().toString();

        if(getUserName.equals("")) {
            Toast.makeText(this, "userName is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(getBio.equals("")) {
            Toast.makeText(this, "bio is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("please wait");
            progressDialog.show();

            HashMap<String,Object> fileMap = new HashMap<>();
            fileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            fileMap.put("name",getUserName);
            fileMap.put("status",getBio);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(fileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Intent intent = new Intent(SettingsActivity.this,ContactsActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(SettingsActivity.this, "Profile Settings has been updated.", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }

    private void retriveUserInfo(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String image= dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    String bio = dataSnapshot.child("status").getValue().toString();

                    userNameET.setText(username);
                    userBioET.setText(bio);

                    RelativeLayout line_first  =findViewById(R.id.line_first);

                  //  Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImageView);


                    My.setImage(progress_bar_load,line_first,image,profileImageView);

                   /* Glide.with(context)
                            .load(postsClass.getPostImageUrl())
                            .error(R.color.gray1)
                            .fitCenter()
                            //.centerCrop()
                            .placeholder(R.color.gray1)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                                    holder.PostImage.setBackgroundResource(R.color.gray1);
                                    holder.progressBarLoad.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    holder.progressBarLoad.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(holder.PostImage);*/

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERYPicker && resultCode ==RESULT_OK  && data!=null){
            ImageUri = data.getData();

            profileImageView.setImageURI(ImageUri);
        }
    }

    private void addOrRemoveProperty(View view, int property, boolean flag){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if(flag){
            layoutParams.addRule(property);
        }else {
            //layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }


}
