package com.happytrees.firebasechatexample;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//NOTES:
//in order to remove user from firebase . you need remove him both from database and authentication menus


// TODO :
//Add option to pick image from camera

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mAuth = FirebaseAuth.getInstance();//returns singleton Instance of FirebaseAuth

        //ACTION BAR
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Chat");



        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkPurple));

        //TABS
        viewPager = findViewById(R.id.MainTabPager);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);




    }

    @Override
    protected void onStart() {
        super.onStart();
        //CHECK IF USER ALREADY SIGNED IN
        FirebaseUser currentUser = mAuth.getCurrentUser();//if currentUser equals null , then user isn't signed in

        if(currentUser==null) {
          sendToStart();//send to Start Activity to sign - in/up
        }

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);//StartActivity there is where you have sign-in/sign-up options
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout_button)  {
            mAuth.signOut();//sign out from chat
            sendToStart();
            Toast.makeText(MainActivity.this,"Logged-Out",Toast.LENGTH_SHORT).show();
        }else if (item.getItemId()==R.id.main_settings_button) {
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }else if (item.getItemId()==R.id.main_all_button) {//list of users
            Intent usersIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(usersIntent);
        }
        return true;
    }
}
