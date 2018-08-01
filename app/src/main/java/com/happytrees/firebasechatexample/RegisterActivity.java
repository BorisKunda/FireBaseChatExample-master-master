package com.happytrees.firebasechatexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar rToolbar = findViewById(R.id.register_page_toolbar);
        setSupportActionBar(rToolbar);
        getSupportActionBar().setTitle(" Create Account ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//sets up navigation button


        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.DarkPurple));

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();//returns singleton Instance of FirebaseAuth

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);

        mCreateButton = findViewById(R.id.reg_create_btn);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("registering user");
                    progressDialog.setMessage("please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    if (email.contains("@") && email.contains(".com")) {
                        register_user(name, email, password);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "malformed email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void register_user(final String displayName, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //STORE USER TO DATABASE
                    //retrieving current user
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    //retrieving current user's id
                    String uId = current_user.getUid();//get user id
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);//get node location(reference) in your JSON-based firebase database you wish to CRUD
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", displayName);
                    userMap.put("email", email);
                    userMap.put("password", password);
                    userMap.put("image", "default");
                    userMap.put("status","hey I am using ChatApp");
                    userMap.put("thumb_image", "default");
                    //FirebaseDatabase.getInstance().setPersistenceEnabled(true); enable for database cashing
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {//set value of user's id object by defining its variables values written in hashmap(setValue -> create or update the value according to Json node provided.)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Authentication succeeded", Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//finishes all activities in stack and creates MainActivity activity
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Exception registerException = task.getException();
                    if (registerException instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(RegisterActivity.this, "Weak password try another one", Toast.LENGTH_SHORT).show();
                    } else if (registerException instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(RegisterActivity.this, "malformed email", Toast.LENGTH_SHORT).show();
                    } else if (registerException instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, "email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }
}

