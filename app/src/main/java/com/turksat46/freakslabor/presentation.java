package com.turksat46.freakslabor;

import android.Manifest;
import android.app.Presentation;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class presentation extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int BLUETOOTH_PERMISSION_CODE = 102;
    private static final int LOCATION_PERMISSION_CODE = 103;
    private static final int BLUETOOTH_ADVERTISE_PERMISSION_CODE = 104;
    private static final int BLUETOOTH_SCAN_PERMISSION_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_presentation);
        constraintLayout = (ConstraintLayout) findViewById(R.id.background);
        checkPermission(Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION_CODE);
        checkPermission(Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_PERMISSION_CODE);
        //checkPermission(Manifest.permission.BLUETOOTH_ADVERTISE, BLUETOOTH_ADVERTISE_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
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
                Toast.makeText(presentation.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(presentation.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
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