package com.pack.QuickTap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class PlayerStats {

    public boolean achievements[];

    public int correctPlays,
            wrongPlays,
            consecutiveDays,
            lastOnlineDay,
            bestReactionTime,
            worstReactionTime,
            multiPlayerGames,
            completedObjectives,
            randomClicks,
            background;

    public PlayerStats() {
        this.achievements = new boolean[27];
        //this.achievements = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.correctPlays = 0;
        this.wrongPlays = 0;
        this.consecutiveDays = 1;
        this.lastOnlineDay = 0;
        this.bestReactionTime = Integer.MAX_VALUE;
        this.worstReactionTime = Integer.MIN_VALUE;
        this.multiPlayerGames = 0;
        this.completedObjectives = 0;
        this.randomClicks = 0;
        this.background = -1;
    }

    public boolean canRandomMode() {
        if (achievements[0] && achievements[11])
            return true;
        return false;
    }

    public boolean canMultiPlayer() {
        if (achievements[1] && achievements[12] && achievements[18])
            return true;
        return false;
    }

    public void checkForRandomMode(Context context) {
        if (canRandomMode())
            randomModeUnlocked(context);
    }

    public void randomModeUnlocked(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("New Game Mode Unlocked!")
                .setMessage("You've just unlocked Random Mode!")
                .create().show();
    }

    public void checkForMultiPlayerMode(Context context) {
        if (canMultiPlayer())
            multiPlayerModeUnlocked(context);
    }

    public void multiPlayerModeUnlocked(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("New Game Mode Unlocked!")
                .setMessage("You've just unlocked MultiPlayer Mode!")
                .create().show();
    }

    public void checkForAchievements(Context context) {
        if (completedObjectives == 27 && !achievements[26]) {
            achievements[26] = true;
            showAlert(26, context);
        }


        if (wrongPlays >= 100 && !achievements[25]) {
            achievements[25] = true;
            showAlert(25, context);
        }
        if (worstReactionTime >= 1000000 && !achievements[24]) {
            achievements[24] = true;
            showAlert(24, context);
        }


        if (multiPlayerGames >= 100000 && !achievements[23]) {
            achievements[23] = true;
            showAlert(23, context);
        }
        if (multiPlayerGames >= 1000 && !achievements[22]) {
            achievements[22] = true;
            showAlert(22, context);
        }
        if (multiPlayerGames >= 100 && !achievements[21]) {
            achievements[21] = true;
            showAlert(21, context);
        }


        if (randomClicks >= 30 && !achievements[20]) {
            achievements[20] = true;
            showAlert(20, context);
        }
        if (randomClicks >= 25 && !achievements[19]) {
            achievements[19] = true;
            showAlert(19, context);
        }
        if (randomClicks >= 20 && !achievements[18]) {
            achievements[18] = true;
            checkForMultiPlayerMode(context);
            showAlert(18, context);
        }
        if (randomClicks >= 15 && !achievements[17]) {
            achievements[17] = true;
            showAlert(17, context);
        }
        if (randomClicks >= 10 && !achievements[16]) {
            achievements[16] = true;
            showAlert(16, context);
        }
        if (randomClicks >= 5 && !achievements[15]) {
            achievements[15] = true;
            showAlert(15, context);
        }


        if (bestReactionTime <= 50 && !achievements[14]) {
            achievements[14] = true;
            showAlert(14, context);
        }
        if (bestReactionTime <= 100 && !achievements[13]) {
            achievements[13] = true;
            showAlert(13, context);
        }
        if (bestReactionTime <= 200 && !achievements[12]) {
            achievements[12] = true;
            checkForMultiPlayerMode(context);
            showAlert(12, context);
        }
        if (bestReactionTime <= 300 && !achievements[11]) {
            achievements[11] = true;
            checkForRandomMode(context);
            showAlert(11, context);
        }
        if (bestReactionTime <= 400 && !achievements[10]) {
            achievements[10] = true;
            showAlert(10, context);
        }
        if (bestReactionTime <= 500 && !achievements[9]) {
            achievements[9] = true;
            showAlert(9, context);
        }


        if (consecutiveDays >= 100 && !achievements[8]) {
            achievements[8] = true;
            showAlert(8, context);
        }
        if (consecutiveDays >= 10 && !achievements[7]) {
            achievements[7] = true;
            showAlert(7, context);
        }
        if (consecutiveDays >= 5 && !achievements[6]) {
            achievements[6] = true;
            showAlert(6, context);
        }


        if (correctPlays >= 100000 && !achievements[5]) {
            achievements[5] = true;
            showAlert(5, context);
        }
        if (correctPlays >= 5000 && !achievements[4]) {
            achievements[4] = true;
            showAlert(4, context);
        }
        if (correctPlays >= 1000 && !achievements[3]) {
            achievements[3] = true;
            showAlert(3, context);
        }
        if (correctPlays >= 100 && !achievements[2]) {
            achievements[2] = true;
            showAlert(2, context);
        }
        if (correctPlays >= 50 && !achievements[1]) {
            achievements[1] = true;
            checkForMultiPlayerMode(context);
            showAlert(1, context);
        }
        if (correctPlays >= 5 && !achievements[0]) {
            achievements[0] = true;
            checkForRandomMode(context);
            showAlert(0, context);
        }
    }

    private void showAlert(int i, final Context context) {
        String message = "";
        final int position = i;
        switch (i) {
            case 0:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[0];
                break;
            case 1:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[1];
                break;
            case 2:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[2];
                break;
            case 3:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[3];
                break;
            case 4:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[4];
                break;
            case 5:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[5];
                break;
            case 6:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[6];
                break;
            case 7:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[7];
                break;
            case 8:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[8];
                break;
            case 9:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[9];
                break;
            case 10:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[10];
                break;
            case 11:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[11];
                break;
            case 12:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[12];
                break;
            case 13:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[13];
                break;
            case 14:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[14];
                break;
            case 15:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[15];
                break;
            case 16:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[16];
                break;
            case 17:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[17];
                break;
            case 18:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[18];
                break;
            case 19:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[19];
                break;
            case 20:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[20];
                break;
            case 21:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[21];
                break;
            case 22:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[22];
                break;
            case 23:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[23];
                break;
            case 24:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[24];
                break;
            case 25:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[25];
                break;
            case 26:
                message = context.getResources().getStringArray(R.array.newAchievementMessage)[26];
                break;
        }
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.newAchievement))
                .setMessage(message + "\n\nYou can now set a new Background Theme in the Achievements Tab")
                .create().show();
    }
}
