package com.example.QuickTap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;

public class MainPageActivity extends AppCompatActivity {

    TextView classicMode, randomMode, multiPlayerMode;
    ImageView achievements;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        String json = sharedPref.getString("achievements", null);
        if (json == null) {
            createAchievements();
        }

        classicMode = findViewById(R.id.classicMode);
        randomMode = findViewById(R.id.randomMode);
        multiPlayerMode = findViewById(R.id.multMode);
        achievements = findViewById(R.id.achievements);

        setClickListeners();
    }

    private void setClickListeners() {
        classicMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, ClassicModeActivity.class));
            }
        });

        randomMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, RandomModeActivity.class));
            }
        });

        multiPlayerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, MultiPlayerModeActivity.class));
            }
        });

        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, AchievementsActivity.class));
            }
        });
    }

    private void createAchievements() {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(new boolean[24]);
        editor.putString("achievements", json);
        editor.apply();
    }

}