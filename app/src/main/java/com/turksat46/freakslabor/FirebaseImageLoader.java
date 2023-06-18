package com.turksat46.freakslabor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseImageLoader {



    public static void loadImage(Context context, String android_id, CircleImageView imageView){
        String imageurl = null;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageReference.child(android_id).child("profilepic");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Picasso.get().load(uri.toString()).into(imageView);



                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


                Glide.with(context)
                        .load(uri)
                        .apply(requestOptions)
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /*


         */

    }

    public static void loadImage(Context context, String android_id, ImageView imageView){
        String imageurl = null;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageReference.child(android_id).child("profilepic");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Picasso.get().load(uri.toString()).into(imageView);



                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);// Cache the image data only


                Glide.with(context)
                        .load(uri)
                        .apply(requestOptions)
                        .into(imageView);
                Log.w("imageurl", uri.toString());
            }
        });

        /*


         */

    }

}
