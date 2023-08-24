package com.turksat46.freakslabor;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDataGrabber {


    public static void getName(String id, TextView textView){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = null;
                        name = documentSnapshot.getString("name");
                        textView.setText(name);
                    }
                });

    }

    public static void getBio(String id, TextView textView){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String bio = null;
                        bio = documentSnapshot.getString("bio");
                        textView.setText(bio);
                    }
                });

    }
}
