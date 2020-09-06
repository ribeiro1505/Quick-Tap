package com.pack.QuickTap;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class MainPageActivity extends AppCompatActivity {

    TextView classicMode, randomMode, multiPlayerMode;
    ImageView achievements;

    PlayerStats playerStats;

    SharedPreferences sharedPref;
    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_page);

        loadAds();

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        //sharedPref.edit().putString("PlayerStats", null).apply();
        String json = sharedPref.getString("PlayerStats", null);
        if (json == null) {
            createPlayerStats();
        } else {
            Type type = new TypeToken<PlayerStats>() {
            }.getType();
            playerStats = new Gson().fromJson(json, type);
        }
        checkDate();

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

    private void checkDate() {
        Calendar c = Calendar.getInstance();
        int thisDay = c.get(Calendar.DAY_OF_YEAR);
        int lastDay = playerStats.lastOnlineDay;


        if (lastDay == thisDay - 1 ||
                (lastDay == 365 && thisDay == 1) ||
                (lastDay == 366 && thisDay == 1)) {
            playerStats.consecutiveDays++;
            new AlertDialog.Builder(this)
                    .setMessage("You've been playing for " + playerStats.consecutiveDays + " consecutive days!\n" +
                            "Keep playing daily to win Achievements!")
                    .show();
            playerStats.checkForAchievements(this);
        } else if (lastDay != thisDay) {
            playerStats.consecutiveDays = 1;
        }
        playerStats.lastOnlineDay = thisDay;
        updatePlayerStats();
    }

    private void createPlayerStats() {
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = gson.toJson(new PlayerStats());

        Type type = new TypeToken<PlayerStats>() {
        }.getType();
        playerStats = new Gson().fromJson(json, type);

        editor.putString("PlayerStats", json);
        editor.apply();
    }

    private void updatePlayerStats() {
        playerStats.checkForAchievements(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = gson.toJson(playerStats);
        editor.putString("PlayerStats", json);
        editor.apply();
    }

    private void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}