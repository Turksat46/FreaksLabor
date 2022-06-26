package com.turksat46.freakslabor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turksat46.freakslabor.ui.main.SetupFragment;

public class setup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SetupFragment.newInstance())
                    .commitNow();
        }
    }
}