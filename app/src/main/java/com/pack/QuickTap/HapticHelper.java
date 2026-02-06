package com.pack.QuickTap;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class HapticHelper {

    @SuppressWarnings("deprecation")
    public static void vibrateShot(Context context) {
        SettingsManager sm = new SettingsManager(context);
        if (!sm.isVibrationEnabled()) return;

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(40);
        }
    }
}
