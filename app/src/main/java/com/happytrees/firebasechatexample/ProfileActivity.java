package com.happytrees.firebasechatexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String mCurrent_state;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final String user_id = getIntent().getStringExtra("user_id");//id of profile clicked in users list activity
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        final ImageView profileIV = findViewById(R.id.profileImage);
        final TextView profileName = findViewById(R.id.profileName);
        final TextView profileStatus = findViewById(R.id.profileStatus);
        TextView profileFriendsCount = findViewById(R.id.profileTotalFriends);
        final Button mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);


        mCurrent_state = "not_friends";


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profile_name = dataSnapshot.child("name").getValue().toString();
                String profile_status = dataSnapshot.child("status").getValue().toString();
                String profile_image = dataSnapshot.child("thumb_image").getValue().toString();

                profileName.setText(profile_name);
                profileStatus.setText(profile_status);

                if (!profile_image.equals("default")) {
                    Picasso.get().load(profile_image).into(profileIV);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STATE - NO FRIENDS
                mProfileSendReqBtn.setEnabled(false);//disable button upon clicking
                if (mCurrent_state.equals("not_friends")) {
                 mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        mProfileSendReqBtn.setEnabled(true);
                                        mCurrent_state="req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");//cancel friend request
                                        Toast.makeText(ProfileActivity.this,"Request Received Successfully" ,Toast.LENGTH_SHORT ).show();
                                    }else{
                                        mProfileSendReqBtn.setEnabled(true);
                                        Toast.makeText(ProfileActivity.this,"Failed Sending Request..." ,Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });
                         }else{
                             Toast.makeText(ProfileActivity.this,"Failed Sending Request..." ,Toast.LENGTH_SHORT ).show();
                         }
                     }
                 });//mCurrent_user --> currently signed -in user .user_id --> id of profile we currently browsing
                }


                //STATE - CANCEL REQUEST
                if(mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {//will remove node : current user -> profile user -> user type -> send
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                 mCurrent_state = "not_friends";
                                 mProfileSendReqBtn.setText("Send Friend Request");
                                 mProfileSendReqBtn.setEnabled(true);
                                }
                            });
                        }
                    });
                }

            }
        });

    }
}
