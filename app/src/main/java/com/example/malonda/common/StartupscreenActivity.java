package com.example.malonda.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.malonda.R;

public class StartupscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, SplashscreenActivity.class));

            finish();
            overridePendingTransition(0, 0);

        }, 500);
    }
}