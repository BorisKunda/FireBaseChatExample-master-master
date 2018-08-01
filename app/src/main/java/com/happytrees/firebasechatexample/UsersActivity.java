package com.happytrees.firebasechatexample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private Toolbar usersToolbar;
    private RecyclerView mUsersList;
    private  DatabaseReference usersdRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);





        //ACTION BAR
        usersToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(usersToolbar);
        getSupportActionBar().setTitle(" All Users ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(usersToolbar);



        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkPurple));

        //setting recycler view
        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));



        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        usersdRef = rootRef.child("Users");




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(User.class,R.layout.users_single_layout,UsersViewHolder.class, usersdRef) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setName(model.name);
                viewHolder.setStatus(model.status);
                viewHolder.setImage(model.thumb_image);

                final String user_id = getRef(position).getKey();//id
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {//sets whole user item as clickable
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent  = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);
                    //Toast.makeText(UsersActivity.this, user_id,Toast.LENGTH_LONG ).show();
                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class  UsersViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }
        public void setName(String name) {
            TextView userNameView = myView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setStatus(String status) {
            TextView userStatusView = myView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public void setImage(String thumb_image) {
            CircleImageView userImageView = myView.findViewById(R.id.user_single_image);
            if(!thumb_image.equals("default")){
                Picasso.get().load(thumb_image).into(userImageView);
            }

        }
    }
}
