package com.happytrees.firebasechatexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//Welcome To The Chat App
public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button alreadyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = findViewById(R.id.start_reg_button);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        alreadyButton = findViewById(R.id.alreadyButton);
        alreadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(StartActivity.this,LoginActivity.class);//StartActivity there is where you have sign-in/sign-up options
                startActivity(startIntent);

            }
        });


    }
}
