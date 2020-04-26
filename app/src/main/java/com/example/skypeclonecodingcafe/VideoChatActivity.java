package com.example.skypeclonecodingcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity
        implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_Key ="";
    private static String SESSION_ID = "";
    private static String TOKEN = "";
    private static String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final  int RC_VIDEO_APP_PERMISSION = 124;
    private DatabaseReference userRef;

    private String userId = "";

    private ImageView closeVideoChatBtn;
    private FrameLayout mPublisherViewController,mSubscriberViewController;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_user));

        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        mPublisherViewController = findViewById(R.id.publisher_container);
        mSubscriberViewController = findViewById(R.id.subscriber_contener);

        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(userId).hasChild("Ringing")){

                            userRef.child(userId).child("Ringing").removeValue();

                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }

                        if(dataSnapshot.child(userId).hasChild("Calling")){

                            userRef.child(userId).child("Calling").removeValue();

                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }
                        else {

                            if(mPublisher!=null){
                                mPublisher.destroy();
                            }

                            if(mSubscriber!=null){
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this,RegistrationActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,VideoChatActivity.this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERMISSION)
    private void requestPermissions(){

        String[] perms ={Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

        if(EasyPermissions.hasPermissions(VideoChatActivity.this,perms))
        {
            mSession = new Session.Builder(this,API_Key,SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);

            mSession.connect(TOKEN);
        }
        else {
            EasyPermissions.requestPermissions(this,"Hey this app needs Mic and Camera permission, please allow.",RC_VIDEO_APP_PERMISSION,perms);
        }

    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //2. Publisshing a stream to the session
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG,"Session connectd");

        mPublisher = new Publisher.Builder(VideoChatActivity.this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherViewController.addView(mPublisher.getView());

        if(mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG,"Stream disconncted");
    }

    // 3. Subscribed to the streams
    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(LOG_TAG,"Stream Received");

        if(mSubscriber==null){
            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG,"Stream drop");

        if(mSubscriber!=null){
            mSubscriber =null;
            mSubscriberViewController.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.i(LOG_TAG,"Stream error");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
