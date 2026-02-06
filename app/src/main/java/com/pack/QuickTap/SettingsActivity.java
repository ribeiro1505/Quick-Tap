package com.pack.QuickTap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;

public class SettingsActivity extends BaseActivity {

    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsManager = new SettingsManager(this);

        setupBackButton();
        setupThemeRadioGroup();
        setupSoundToggle();
        setupVibrationToggle();
        setupDeveloperSection();
        loadAds();
    }

    private void setupBackButton() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void setupThemeRadioGroup() {
        RadioGroup themeGroup = findViewById(R.id.themeRadioGroup);

        int currentMode = settingsManager.getThemeMode();
        switch (currentMode) {
            case SettingsManager.THEME_LIGHT:
                themeGroup.check(R.id.radioLight);
                break;
            case SettingsManager.THEME_DARK:
                themeGroup.check(R.id.radioDark);
                break;
            default:
                themeGroup.check(R.id.radioSystem);
                break;
        }

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.radioLight) {
                mode = SettingsManager.THEME_LIGHT;
            } else if (checkedId == R.id.radioDark) {
                mode = SettingsManager.THEME_DARK;
            } else {
                mode = SettingsManager.THEME_SYSTEM;
            }
            settingsManager.setThemeMode(mode);
            SettingsManager.applyTheme(this);
        });
    }

    private void setupSoundToggle() {
        SwitchCompat switchSound = findViewById(R.id.switchSound);
        switchSound.setChecked(settingsManager.isSoundEnabled());
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsManager.setSoundEnabled(isChecked));
    }

    private void setupVibrationToggle() {
        SwitchCompat switchVibration = findViewById(R.id.switchVibration);
        switchVibration.setChecked(settingsManager.isVibrationEnabled());
        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsManager.setVibrationEnabled(isChecked));
    }

    private void setupDeveloperSection() {
        View cardDeveloper = findViewById(R.id.cardDeveloper);

        if (!BuildConfig.DEBUG) return;

        cardDeveloper.setVisibility(View.VISIBLE);

        findViewById(R.id.btnUnlockAll).setOnClickListener(v -> {
            PlayerStats stats = loadPlayerStats();
            if (stats == null) return;

            Arrays.fill(stats.achievements, true);
            stats.completedObjectives = 27;
            stats.correctPlays = Math.max(stats.correctPlays, 100000);
            stats.bestReactionTime = Math.min(stats.bestReactionTime, 50);
            stats.worstReactionTime = Math.max(stats.worstReactionTime, 1000001);
            stats.wrongPlays = Math.max(stats.wrongPlays, 100);
            stats.consecutiveDays = Math.max(stats.consecutiveDays, 100);
            stats.randomClicks = Math.max(stats.randomClicks, 30);
            stats.multiPlayerGames = Math.max(stats.multiPlayerGames, 100000);
            savePlayerStats(stats);

            Toast.makeText(this, "All unlocked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLockAll).setOnClickListener(v -> {
            PlayerStats stats = new PlayerStats();
            savePlayerStats(stats);

            Toast.makeText(this, "All reset", Toast.LENGTH_SHORT).show();
        });
    }

    private PlayerStats loadPlayerStats() {
        SharedPreferences prefs = getSharedPreferences("GameFile", MODE_PRIVATE);
        String json = prefs.getString("PlayerStats", null);
        if (json == null) return null;
        Type type = new TypeToken<PlayerStats>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private void savePlayerStats(PlayerStats stats) {
        SharedPreferences prefs = getSharedPreferences("GameFile", MODE_PRIVATE);
        prefs.edit().putString("PlayerStats", new Gson().toJson(stats)).apply();
    }

    private void loadAds() {
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
