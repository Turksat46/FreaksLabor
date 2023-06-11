package com.turksat46.freakslabor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class setupActivity extends AppCompatActivity {

    Button savebutton;
    GoogleSignInAccount account;
    ImageView userimg;
    private Uri filePath;

    String idToken;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        savebutton = (Button) findViewById(R.id.savebutton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save everything on database
                saveUser();
            }
        });

        Intent intent = getIntent();
        idToken = intent.getStringExtra("id");

        userimg = (ImageView) findViewById(R.id.profileuserimg);
        userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 1540);
                intent.putExtra("outputY", 1540);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (requestCode == 2
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData();

            /* Get the Uri of data

            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                userimg.setImageBitmap(bitmap);

                /*Save image to database
                Uri uri=data.getData();
                StorageReference filepath=storage.child("Images").child(uri.getLastPathSegment());
                storageReference.child("Images").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        newStudent.child("image").setValue(downloadUrl);
                    }
                });

                 */
/*

            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        */
        }

    }


    private void saveUser() {
        StorageReference imageRef = storageRef.child(idToken + "/image.jpg"); // Set the desired path for your image
        UploadTask uploadTask = imageRef.putFile(filePath); // If you have the file URI
        // Or: imageRef.putFile(file); // If you have the file object directly
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                // You can get the image download URL if needed:
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        // Do something with the download URL
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Image upload failed
            }
        });

    }

}