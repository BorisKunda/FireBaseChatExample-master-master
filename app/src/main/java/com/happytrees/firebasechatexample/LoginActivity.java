package com.happytrees.firebasechatexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth lgnAuth;

    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lgnAuth = FirebaseAuth.getInstance();//returns singleton Instance of FirebaseAuth

        Toolbar mToolbar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Login ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//sets up navigation button

        loginProgressDialog = new ProgressDialog(this);

        final EditText passwordEdt  = findViewById(R.id.lEdtPassword);
        final EditText emailEdt = findViewById(R.id.lEdtEmail);

        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkPurple));

        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEdt.getText().toString();
                String email =emailEdt.getText().toString();
                if(TextUtils.isEmpty(password)||TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                }else{
                    loginProgressDialog.setTitle("Logging in");
                    loginProgressDialog.setMessage("please wait...");
                    loginProgressDialog.setCanceledOnTouchOutside(false);
                    loginProgressDialog.show();
                    loginUser(password,email);
                }
            }
        });
    }

    private void loginUser(String password, String email) {
        lgnAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
             if (task.isSuccessful()) {
                 loginProgressDialog.dismiss();
                 Intent startIntent = new Intent(LoginActivity.this,MainActivity.class);//StartActivity there is where you have sign-in/sign-up options
                 startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//finishes all activities in stack and creates MainActivity activity
                 startActivity(startIntent);
                 finish();
                 Toast.makeText(LoginActivity.this,"Signing-in success",Toast.LENGTH_SHORT).show();
             }else{
                 loginProgressDialog.dismiss();
                 Exception loginException = task.getException();
                 if(loginException instanceof FirebaseAuthInvalidUserException) {
                     Toast.makeText(LoginActivity.this,"wrong email",Toast.LENGTH_SHORT).show();
                 }else if(loginException instanceof FirebaseAuthInvalidCredentialsException) {
                     if(loginException.getMessage().equals("The email address is badly formatted.")){
                         Toast.makeText(LoginActivity.this,"wrong password and email",Toast.LENGTH_SHORT).show();
                     }else{
                         Toast.makeText(LoginActivity.this,"wrong password",Toast.LENGTH_SHORT).show();
                     }
                 }else{
                     Toast.makeText(LoginActivity.this,"Signing-in failure",Toast.LENGTH_SHORT).show();
                 }
             }
            }
        });
    }
}
