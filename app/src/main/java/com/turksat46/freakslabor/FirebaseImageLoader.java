package com.turksat46.freakslabor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseImageLoader {

    static int gradientColor = 0;

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

    public static void loadImage(Context context, String android_id, ImageView imageView) {
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
    }

    public static void loadImage(Context context, String android_id, ImageView imageView, View background){
        String imageurl = null;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageReference.child(android_id).child("profilepic");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Picasso.get().load(uri.toString()).into(imageView);



                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);// Cache the image data only


                Glide.with(context.getApplicationContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(imageView)
                        .setRequest(new Request() {
                            @Override
                            public void begin() {
                                
                            }

                            @Override
                            public void clear() {

                            }

                            @Override
                            public void pause() {

                            }

                            @Override
                            public boolean isRunning() {
                                return false;
                            }

                            @Override
                            public boolean isComplete() {
                                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap ();

                                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette palette) {
                                        // Do something with colors...
                                        gradientColor = palette.getDominantColor(Color.BLUE);

                                    }
                                });

                                final View view = background;
                                Drawable[] layers = new Drawable[1];

                                ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
                                    @Override
                                    public Shader resize(int width, int height) {
                                        LinearGradient lg = new LinearGradient(
                                                0,
                                                0,
                                                0,
                                                view.getHeight(),
                                                new int[] {
                                                        gradientColor, // please input your color from resource for color-4
                                                        Color.parseColor("#000000"),
                                                },
                                                new float[] { 0.09f, 1.5f },
                                                Shader.TileMode.CLAMP);
                                        return lg;
                                    }
                                };
                                PaintDrawable p = new PaintDrawable();
                                p.setShape(new RectShape());
                                p.setShaderFactory(sf);
                                p.setCornerRadii(new float[] { 1, 4, 4, 1, 0, 0, 0, 0 });
                                layers[0] = (Drawable) p;

                                LayerDrawable composite = new LayerDrawable(layers);
                                view.setBackgroundDrawable(composite);


                                return false;
                            }

                            @Override
                            public boolean isCleared() {
                                return false;
                            }

                            @Override
                            public boolean isAnyResourceSet() {
                                return false;
                            }

                            @Override
                            public boolean isEquivalentTo(Request other) {
                                return false;
                            }
                        });
                Log.w("imageurl", uri.toString());
            }
        });


    }

}
