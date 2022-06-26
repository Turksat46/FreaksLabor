package com.turksat46.freakslabor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ownprofile extends AppCompatActivity {

    CircleImageView profileimg;
    ImageView wirelessImageView;
    Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getSupportActionBar().hide();
        // set an exit transition
        getWindow().setExitTransition(new AutoTransition().addTarget(profileimg));
        setContentView(R.layout.activity_ownprofile);
        // inside your activity (if you did not enable transitions in your theme)

        profileimg = (CircleImageView)findViewById(R.id.profileimg);

        settingsButton = (Button)findViewById(R.id.button2);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ownprofile.this, SettingsActivity.class));
            }
        });




    }
}