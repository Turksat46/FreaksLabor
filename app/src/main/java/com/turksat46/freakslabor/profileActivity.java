package com.turksat46.freakslabor;

import android.content.Intent;
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
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profileActivity extends AppCompatActivity {

    boolean online = false;
    int id = 0;


    FloatingActionButton backButton;

    ImageView backButton2;
    ImageView profileimg;
    TextView nameHolder;
    TextView bioHolder;
    int gradientColor = 0;

    String uid;
    String name;
    String bio;

    Friendship friendshipManager;
    Button addAsFriendButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        handleIntent(getIntent());

        friendshipManager = new Friendship(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        addAsFriendButton = (Button) findViewById(R.id.addAsFriendButton);
        addAsFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendshipManager.sendFriendRequest(uid);
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }


        backButton = (FloatingActionButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(profileActivity.this, MainActivity.class);
                //startActivity(i);
                finish();
            }
        });

        backButton2 = (ImageView)findViewById(R.id.imageView5);
        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileimg = (ImageView)findViewById(R.id.largeprofpic);

        nameHolder=(TextView)findViewById(R.id.profileusernameText);
        bioHolder = (TextView)findViewById(R.id.bioText);

        Bundle b = getIntent().getExtras();
        uid = String.valueOf(b.get("uid"));

        initRecyclerView();

        Handler mHandler = new Handler();


        FirebaseImageLoader.loadImage(this, uid, profileimg, findViewById(R.id.background));

        Bitmap bitmap = ((BitmapDrawable)profileimg.getDrawable()).getBitmap ();
        Bitmap bmp = ((BitmapDrawable) getResources()
                .getDrawable(R.drawable.img)).getBitmap();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = (String) documentSnapshot.get("name");
                        bio = (String) documentSnapshot.get("bio");
                        setText();

                    }
                });
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            String userId = appLinkData.getLastPathSegment();
            uid = userId;
        }
    }

    private void getUser(){

    }

    private void setText() {
        nameHolder.setText(name);
        bioHolder.setText(bio);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.batcheslist);
        recyclerView.setLayoutManager(layoutManager);
        BadgesViewAdapter adapter = new BadgesViewAdapter(this);
        recyclerView.setAdapter(adapter);

    }
}