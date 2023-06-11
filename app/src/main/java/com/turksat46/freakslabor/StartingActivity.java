package com.turksat46.freakslabor;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.turksat46.freakslabor.databinding.ActivityStartingBinding;

public class StartingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        getSupportActionBar().hide();

        sharedPreferences= getSharedPreferences("cred",MODE_PRIVATE);

        final VideoView videoview = (VideoView) findViewById(R.id.videoView2);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.logoanim);
        videoview.setVideoURI(uri);
        videoview.setAudioFocusRequest(0);
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                /*if(account == null){
                    videoview.suspend();
                    Intent setupintent = new Intent(StartingActivity.this, setup.class);
                    startActivity(setupintent);
                    finish();
                }else {
                    videoview.suspend();
                    Intent intent = new Intent(StartingActivity.this, newMainActivity.class);
                    startActivity(intent);
                    finish();
                }


                 */
                if(sharedPreferences.contains("id")){
                    videoview.suspend();
                    Intent intent = new Intent(StartingActivity.this, newMainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    videoview.suspend();
                    Intent intent = new Intent(StartingActivity.this, setup.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoview.start();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


}