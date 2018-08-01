package com.happytrees.firebasechatexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Toolbar statusToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(statusToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//sets up navigation button


        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkPurple));


        Intent intentFromSettings = getIntent();
        String newStatus = intentFromSettings.getStringExtra("status value");
        final TextInputLayout textInputStatus = findViewById(R.id.statusTextInput);
        textInputStatus.getEditText().setText(newStatus);
        Button saveStatus = findViewById(R.id.btnSaveStatus);
        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = textInputStatus.getEditText().getText().toString();
                if(TextUtils.isEmpty(status)) {
                    Toast.makeText(StatusActivity.this,"Please Write Something",Toast.LENGTH_LONG).show();
                }else{
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String currentId = firebaseUser.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentId).child("status");
                    final ProgressDialog progressDialog = new ProgressDialog(StatusActivity.this);
                    progressDialog.setTitle("Saving Status");
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    databaseReference.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                finish();
                            }else{
                                Toast.makeText(StatusActivity.this,"there was some error in data saving",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }


            }
        });
    }
}
