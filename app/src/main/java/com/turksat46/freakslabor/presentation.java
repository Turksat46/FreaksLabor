package com.turksat46.freakslabor;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class presentation extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;
    Button continueButton;
    Button abortButton;

    ImageView logoViewImage;
    ImageView bigLogoView;

    TextView descriptionText;
    TextView permissionText;



    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int BLUETOOTH_PERMISSION_CODE = 102;
    private static final int LOCATION_PERMISSION_CODE = 103;
    private static final int BLUETOOTH_ADVERTISE_PERMISSION_CODE = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        // set an exit transition
        getWindow().setExitTransition(new Fade());
        getSupportActionBar().hide();
        setContentView(R.layout.activity_presentation);

        if(getSupportActionBar() != null) {
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

        continueButton = (Button)findViewById(R.id.agreeButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = null;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    permissions = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

                }else{
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
                }
                requestPermissions(permissions, 100);
                //checkPermissions();

            }
        });
        abortButton = (Button)findViewById(R.id.abortButton);
        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        constraintLayout = (ConstraintLayout) findViewById(R.id.background);

        logoViewImage = (ImageView) findViewById(R.id.logoImageView);
        bigLogoView = (ImageView) findViewById(R.id.imageView4);
        descriptionText = (TextView) findViewById(R.id.descriptionView);
        permissionText = (TextView) findViewById(R.id.permissionTextView);

        permissionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Turksat46/FreaksLabor/blob/master/eula.md"));
                startActivity(openLink);
            }
        });

        Handler mHandler = new Handler();
        mHandler.postDelayed(()->{
            bigLogoView.setVisibility(View.GONE);
            logoViewImage.setVisibility(View.VISIBLE);
            //Play Startup sound
            MediaPlayer mp = MediaPlayer.create(presentation.this, R.raw.startup);
            mp.setLooping(false);
            mp.start();

            if(ContextCompat.checkSelfPermission(presentation.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(presentation.this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_DENIED||
                    ContextCompat.checkSelfPermission(presentation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED||
                    ContextCompat.checkSelfPermission(presentation.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED||
                    ContextCompat.checkSelfPermission(presentation.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                descriptionText.setVisibility(View.VISIBLE);
                permissionText.setVisibility(View.VISIBLE);
                abortButton.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);


            }else{
                mHandler.postDelayed(()->{
                    Intent intent = new Intent(presentation.this, MainActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    finish();
                }, 2000);
            }

        }, 5000);

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

    private void checkPermissions() {

        checkPermission(Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_PERMISSION_CODE);
        checkPermission(Manifest.permission.BLUETOOTH_ADVERTISE, BLUETOOTH_ADVERTISE_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(presentation.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(presentation.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(presentation.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(presentation.this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(presentation.this, setupActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                finish();
            } else {
                Toast.makeText(presentation.this, "Permissions missing...", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(presentation.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(presentation.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(presentation.this, "Bluetooth Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(presentation.this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(presentation.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(presentation.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == BLUETOOTH_ADVERTISE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(presentation.this, "Bluetooth Advertise Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(presentation.this, "Bluetooth Advertise Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}