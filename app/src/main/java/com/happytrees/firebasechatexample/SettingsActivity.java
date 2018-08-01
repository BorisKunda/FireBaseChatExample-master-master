package com.happytrees.firebasechatexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar sToolbar;
    private DatabaseReference sDatabaseReference;// firebase database reference
    private CircleImageView sDisplayImage;
    private TextView sName;
    private TextView sStatus;
    private String statusFromSettings;
    private String currentUserId;
    private final int GALLERY_PICK = 1;
    private  Bitmap compressedBitmapFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //CHANGE STATUS BAR COLOR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.DarkPurple));

        //ACTION BAR
        sToolbar = findViewById(R.id.account_settings_page_toolbar);
        setSupportActionBar(sToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//sets up navigation button

        sDisplayImage = findViewById(R.id.profile_image);
        sName = findViewById(R.id.AccountName);
        sStatus = findViewById(R.id.AccountStatus);

        //retrieving current user
        FirebaseUser sCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //retrieving current's user id
        currentUserId = sCurrentUser.getUid();
        sDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);//get node location(reference) in your JSON-based firebase database you wish to CRUD
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Account Settings");
        progressDialog.setMessage("Loading...");
       // progressDialog.show();
        sDatabaseReference.addValueEventListener(new ValueEventListener() {//ValueEventListener will be triggered whenever there is a change in data in realtime.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//onDataChange() you can perform the desired operations onto new data.
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                statusFromSettings = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();//"thumb" means thumbnail.
                sName.setText(name);
                sStatus.setText(statusFromSettings);
                if(!thumb_image.equals("default")) {
                    Picasso.get().load(thumb_image).into(sDisplayImage);//download image via url and set it as circular image view
                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {//for handling database errors
                Log.e("FirebaseDatabase Error", databaseError.toString());
            }


        });


        Button changeStatusBtn = findViewById(R.id.changeAccountStatusBtn);
        changeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if there is logged user at all
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {//user currently singed in
                    Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                    statusIntent.putExtra("status value", statusFromSettings);
                    startActivity(statusIntent);
                } else {
                    Toast.makeText(SettingsActivity.this, "User logged off", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button changeImage = findViewById(R.id.changeAccountImageBtn);
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to pick up image from Gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");//sets type of file you want to pick as image
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);//Allow the user to select a particular kind of data and return it.
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK); //createChooser : Convenience function for creating a ACTION_CHOOSER Intent.(ACTION_CHOOSER :)


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();//gets uri of chosen to be cropped image
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);//Will open crop image activity for chosen imaged(based on image Uri).setAspectRatio will limit cropping field to nice little square

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Changing Image");
                progressDialog.setMessage("please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
              //  progressDialog.show();
                Uri resultUri = result.getUri();//uri of cropped image

                File thumb_filePath = new File(resultUri.getPath());//resultUri.getPath()--> gets part of URI which is path
                //image compression
                try {
                    compressedBitmapFile = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(75).compressToBitmap( thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //upload compressed bitmap to firebase cloud storage
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmapFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //storage references
                StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();//get firebase cloud storage instance
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(currentUserId + ".jpg");//location where thumbnails will be uploaded
                StorageReference filePath = mImageStorage.child("profile_images").child(currentUserId+".jpg");//"profile_images" is root directory we defined in our firebase cloud storage."profile image" is the name of future uploaded image
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {//put our cropped image in firebase storage
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()) {

                                        sDatabaseReference.child("thumb_image").setValue(thumb_downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }

                                }
                            });



                        } else {
                            progressDialog.dismiss();
                            Log.e("firebase storage", "failure in uploading cropped image");
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

