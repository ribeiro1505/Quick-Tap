package com.pack.QuickTap;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
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
    ImageView achievements, info;
    ConstraintLayout background;

    PlayerStats playerStats;

    SharedPreferences sharedPref;
    Gson gson = new Gson();

    int[] backgrounds = {R.drawable.backgroun1, R.drawable.backgroun2, R.drawable.backgroun3,
            R.drawable.backgroun4, R.drawable.backgroun5, R.drawable.backgroun6,
            R.drawable.backgroun7, R.drawable.backgroun8, R.drawable.backgroun9,
            R.drawable.backgroun10, R.drawable.backgroun11, R.drawable.backgroun12,
            R.drawable.backgroun13, R.drawable.backgroun14, R.drawable.backgroun15,
            R.drawable.backgroun16, R.drawable.backgroun17, R.drawable.backgroun18,
            R.drawable.backgroun19, R.drawable.backgroun20, R.drawable.backgroun21,
            R.drawable.backgroun22, R.drawable.backgroun23, R.drawable.backgroun24,
            R.drawable.backgroun25, R.drawable.backgroun26, R.drawable.backgroun27};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_page);

        background = findViewById(R.id.layout);

        loadSharedPrefs();
        checkDate();
        loadBackGround();

        classicMode = findViewById(R.id.classicMode);
        randomMode = findViewById(R.id.randomMode);
        multiPlayerMode = findViewById(R.id.multMode);
        achievements = findViewById(R.id.achievements);
        info = findViewById(R.id.info);

        setClickListeners();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadAds();
        loadSharedPrefs();
        loadBackGround();
    }

    private void setClickListeners() {
        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, AchievementsActivity.class));
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, InfoActivity.class));
            }
        });


        classicMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSharedPrefs();
                startActivity(new Intent(MainPageActivity.this, ClassicModeActivity.class));
            }
        });

        randomMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSharedPrefs();
                if (playerStats.canRandomMode())
                    startActivity(new Intent(MainPageActivity.this, RandomModeActivity.class));
                else
                    new AlertDialog.Builder(MainPageActivity.this)
                            .setTitle("Game Mode Locked")
                            .setMessage("To unlock this game mode you must:\n\n" +
                                    ". Play at least 5 correct games in Classic Mode;\n" +
                                    ". Have a reaction time less than 300ms\n")
                            .create().show();
            }
        });

        multiPlayerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSharedPrefs();
                if (playerStats.canMultiPlayer())
                    startActivity(new Intent(MainPageActivity.this, MultiPlayerModeActivity.class));
                else
                    new AlertDialog.Builder(MainPageActivity.this)
                            .setTitle("Game Mode Locked")
                            .setMessage("To unlock this game mode you must:\n\n" +
                                    ". Play at least 50 correct games in Classic Mode;\n" +
                                    ". Have a reaction time less than 200ms\n" +
                                    ". Have at least 20 points in Random Mode")
                            .create().show();
            }
        });
    }

    private void loadSharedPrefs() {
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

    private void loadBackGround(){
        if(playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
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