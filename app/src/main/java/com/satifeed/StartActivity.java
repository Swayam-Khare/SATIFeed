package com.satifeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.satifeed.db.DatabaseHelper;
import com.satifeed.service.NotifyService;

public class StartActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences shared = getSharedPreferences("MyShared", MODE_PRIVATE);
        String isFirstTime = shared.getString("opened", "Yes");

        if (isFirstTime.equals("Yes")){
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("opened", "No");
            editor.apply();
        }

        Intent i = new Intent(StartActivity.this, MainActivity.class);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(i);
            dbHelper.close();
            finish();
        }, 1500);
    }
}