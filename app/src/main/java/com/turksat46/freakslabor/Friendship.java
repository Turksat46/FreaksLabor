package com.turksat46.freakslabor;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Friendship {
    String ownID = null;
    public Friendship(String id){
        ownID = id;
    }

    public void sendFriendRequest(String id){
        //Code Prototype... it could work
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> temp = new HashMap<>();
        temp.put("friendrequest", Arrays.asList(ownID));
        db.collection("users").document(id).set(temp, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.w("Friendship", "Added friendrequest");
                    }
                });
    }

    public Boolean getFriendRequest(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("pfad/zum/stringarray");

        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Arrays array = (Arrays) Arrays.asList(documentSnapshot.get("friendrequests"));


                    }
                });
        return true;
    }
}
