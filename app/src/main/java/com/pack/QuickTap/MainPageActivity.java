package com.pack.QuickTap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class MainPageActivity extends BaseActivity {

    MaterialCardView classicCard, randomCard, multiPlayerCard;
    View settingsButton, achievements, info, gameLogo, gameTitle;
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
        setContentView(R.layout.main_page);

        background = findViewById(R.id.layout);

        loadSharedPrefs();
        checkDate();
        loadBackGround();

        classicCard = findViewById(R.id.classicCard);
        randomCard = findViewById(R.id.randomCard);
        multiPlayerCard = findViewById(R.id.multiPlayerCard);
        achievements = findViewById(R.id.achievements);
        info = findViewById(R.id.info);
        settingsButton = findViewById(R.id.settings);
        gameLogo = findViewById(R.id.gameLogo);
        gameTitle = findViewById(R.id.gameTitle);

        setClickListeners();
        playStaggerAnimation();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadAds();
        loadSharedPrefs();
        loadBackGround();
    }

    private void setClickListeners() {
        achievements.setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, AchievementsActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        info.setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, InfoActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainPageActivity.this, SettingsActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        classicCard.setOnClickListener(v -> {
            loadSharedPrefs();
            startActivity(new Intent(MainPageActivity.this, ClassicModeActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        randomCard.setOnClickListener(v -> {
            loadSharedPrefs();
            if (playerStats.canRandomMode()) {
                startActivity(new Intent(MainPageActivity.this, RandomModeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                new MaterialAlertDialogBuilder(MainPageActivity.this)
                        .setTitle("Game Mode Locked")
                        .setMessage("To unlock this game mode you must:\n\n" +
                                ". Play at least 5 correct games in Classic Mode;\n" +
                                ". Have a reaction time less than 300ms\n")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        multiPlayerCard.setOnClickListener(v -> {
            loadSharedPrefs();
            if (playerStats.canMultiPlayer()) {
                startActivity(new Intent(MainPageActivity.this, MultiPlayerModeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                new MaterialAlertDialogBuilder(MainPageActivity.this)
                        .setTitle("Game Mode Locked")
                        .setMessage("To unlock this game mode you must:\n\n" +
                                ". Play at least 50 correct games in Classic Mode;\n" +
                                ". Have a reaction time less than 200ms\n" +
                                ". Have at least 20 points in Random Mode")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void playStaggerAnimation() {
        View[] views = {gameLogo, gameTitle, classicCard, randomCard, multiPlayerCard};
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.setAlpha(0f);
            v.setTranslationY(30f);

            ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(v, "translationY", 30f, 0f);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(alpha, translationY);
            set.setDuration(300);
            set.setStartDelay(i * 80L);
            set.setInterpolator(new DecelerateInterpolator());
            set.start();
        }
    }

    private void loadSharedPrefs() {
        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
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
            new MaterialAlertDialogBuilder(this)
                    .setMessage("You've been playing for " + playerStats.consecutiveDays + " consecutive days!\n" +
                            "Keep playing daily to win Achievements!")
                    .setPositiveButton("OK", null)
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

    private void loadBackGround() {
        if (playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
    }

    private void loadAds() {
        MobileAds.initialize(this, initializationStatus -> {});

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
