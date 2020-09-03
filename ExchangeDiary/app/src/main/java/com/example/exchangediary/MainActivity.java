package com.example.exchangediary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
        public void GotoLogin(View v){
            ImageButton Main = (ImageButton)findViewById(R.id.Main);

            Main.setBackgroundResource(R.drawable.logooo);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

