package com.turksat46.freakslabor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setupActivity extends AppCompatActivity {

    Button savebutton;
    GoogleSignInAccount account;
    ImageView userimg;
    EditText editname;
    EditText editBio;
    private Uri filePath;

    SharedPreferences sharedPreferences;


    String idToken;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        sharedPreferences= getSharedPreferences("user",MODE_PRIVATE);

        db.setFirestoreSettings(settings);
        savebutton = (Button) findViewById(R.id.savebutton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save everything on database
                saveUser();
            }
        });

        editname = (EditText)findViewById(R.id.editTextTextPersonName);
        editBio = (EditText)findViewById(R.id.editTextBio);

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
            userimg.setImageURI(filePath);
            //generate & save id to sp
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("image", filePath.toString());
            myEdit.apply();
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
q
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        */
        }

    }


    private void saveUser() {
        StorageReference imageRef = storageRef.child(idToken)
                .child("profilepic"); // Set the desired path for your image
        imageRef.putFile(Uri.parse(String.valueOf(filePath))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.w("Firebase Storage", "Upload successful!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firebase Storage", "Upload failed: "+e);
            }
        });


        Map<String, Object> user = new HashMap<>();
        user.put("name", editname.getText().toString());
        user.put("bio", editBio.getText().toString());

        //Save into database
        db.collection("users").document(idToken).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(setupActivity.this, newMainActivity.class));
                    }
                });

    }

}