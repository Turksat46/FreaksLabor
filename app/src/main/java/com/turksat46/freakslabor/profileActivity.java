package com.turksat46.freakslabor;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.turksat46.freakslabor.databinding.ActivityProfileBinding;

public class profileActivity extends AppCompatActivity {

    boolean online = false;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        Bundle b = getIntent().getExtras();
        online = b.getBoolean("online");
        id = b.getInt("id");


    }
}