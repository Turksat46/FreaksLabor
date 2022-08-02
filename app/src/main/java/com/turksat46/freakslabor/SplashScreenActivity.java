package com.turksat46.freakslabor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        //checkPermissions();
        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void checkPermissions(){
        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)){
            Intent intent = new Intent(SplashScreenActivity.this, presentation.class);
            startActivity(intent);
        }

        if(!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Intent intent = new Intent(SplashScreenActivity.this, presentation.class);
            startActivity(intent);
        }
        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(i);
    }

    public boolean checkPermission(String permission){
        if (ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }

}