package com.example.malonda.common;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;

public class ForgetPassword1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password1);
    }

    public void goOptionScreen(View view) {
        onBackPressed();
    }

    public void resetPassword(View view) {
    }
}