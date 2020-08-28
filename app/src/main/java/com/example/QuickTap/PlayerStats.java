package com.example.QuickTap;

import android.app.AlertDialog;
import android.content.Context;

public class PlayerStats {

    Context context;

    public boolean achievements[];

    public int correctPlays,
            wrongPlays,
            consecutiveDays,
            bestReactionTime,
            worstReactionTime,
            multiPlayerGames,
            completedObjectives,
            randomClicks;

    public PlayerStats() {
        this.achievements = new boolean[27];
        this.correctPlays = 0;
        this.wrongPlays = 0;
        this.consecutiveDays = 0;
        this.bestReactionTime = Integer.MAX_VALUE;
        this.worstReactionTime = Integer.MIN_VALUE;
        this.multiPlayerGames = 0;
        this.completedObjectives = 0;
        this.randomClicks = 0;
    }

    public void checkForAchievements(Context context) {
        this.context = context;

        if (correctPlays >= 5 && !achievements[0]) {
            achievements[0] = true;
            showAlert(0);
        }
        if (correctPlays >= 50 && !achievements[1]) {
            achievements[1] = true;
            showAlert(1);
        }
        if (correctPlays >= 100 && !achievements[2]) {
            achievements[2] = true;
            showAlert(2);
        }
        if (correctPlays >= 1000 && !achievements[3]) {
            achievements[3] = true;
            showAlert(3);
        }
        if (correctPlays >= 5000 && !achievements[4]) {
            achievements[4] = true;
            showAlert(4);
        }
        if (correctPlays >= 100000 && !achievements[5]) {
            achievements[5] = true;
            showAlert(5);
        }


        if (consecutiveDays >= 5 && !achievements[6]) {
            achievements[6] = true;
            showAlert(6);
        }
        if (consecutiveDays >= 10 && !achievements[7]) {
            achievements[7] = true;
            showAlert(7);
        }
        if (consecutiveDays >= 100 && !achievements[8]) {
            achievements[8] = true;
            showAlert(8);
        }


        if (bestReactionTime <= 500 && !achievements[9]) {
            achievements[9] = true;
            showAlert(9);
        }
        if (bestReactionTime <= 400 && !achievements[10]) {
            achievements[10] = true;
            showAlert(10);
        }
        if (bestReactionTime <= 300 && !achievements[11]) {
            achievements[11] = true;
            showAlert(11);
        }
        if (bestReactionTime <= 200 && !achievements[12]) {
            achievements[12] = true;
            showAlert(12);
        }
        if (bestReactionTime <= 100 && !achievements[13]) {
            achievements[13] = true;
            showAlert(13);
        }
        if (bestReactionTime <= 50 && !achievements[14]) {
            achievements[14] = true;
            showAlert(14);
        }


        if (randomClicks >= 5 && !achievements[15]) {
            achievements[15] = true;
            showAlert(15);
        }
        if (randomClicks >= 10 && !achievements[16]) {
            achievements[16] = true;
            showAlert(16);
        }
        if (randomClicks >= 15 && !achievements[17]) {
            achievements[17] = true;
            showAlert(17);
        }
        if (randomClicks >= 20 && !achievements[18]) {
            achievements[18] = true;
            showAlert(18);
        }
        if (randomClicks >= 25 && !achievements[19]) {
            achievements[19] = true;
            showAlert(19);
        }
        if (randomClicks >= 30 && !achievements[20]) {
            achievements[20] = true;
            showAlert(20);
        }


        if (multiPlayerGames >= 100 && !achievements[21]) {
            achievements[21] = true;
            showAlert(21);
        }
        if (multiPlayerGames >= 1000 && !achievements[22]) {
            achievements[22] = true;
            showAlert(22);
        }
        if (multiPlayerGames >= 100000 && !achievements[23]) {
            achievements[23] = true;
            showAlert(23);
        }


        if (worstReactionTime >= 1000000 && !achievements[24]) {
            achievements[24] = true;
            showAlert(24);
        }
        if (wrongPlays >= 100 && !achievements[25]) {
            achievements[25] = true;
            showAlert(25);
        }


        if (completedObjectives == 27 && !achievements[26]) {
            achievements[26] = true;
            showAlert(26);
        }
    }

    private void showAlert(int i) {
        String message = "";
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
                .setMessage(message)
                .show();
    }
}
