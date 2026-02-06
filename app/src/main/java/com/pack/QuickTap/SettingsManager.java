package com.pack.QuickTap;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SettingsManager {

    private static final String PREFS_NAME = "GameFile";
    private static final String KEY_THEME_MODE = "settings_theme_mode";
    private static final String KEY_SOUND_ENABLED = "settings_sound_enabled";
    private static final String KEY_VIBRATION_ENABLED = "settings_vibration_enabled";

    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM);
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply();
    }

    public static void applyTheme(Context context) {
        SettingsManager sm = new SettingsManager(context);
        switch (sm.getThemeMode()) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
