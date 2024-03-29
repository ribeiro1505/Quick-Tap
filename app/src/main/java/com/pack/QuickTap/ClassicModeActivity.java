package com.pack.QuickTap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClassicModeActivity extends AppCompatActivity {

    private static final int MIN = 1000;
    private static final int MAX = 5000;

    private SharedPreferences sharedPref;
    private TextView timeCounter, highScoreText;
    private ConstraintLayout background;

    ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;

    Handler handler;
    MediaPlayer mp;
    private InterstitialAd mInterstitialAd;
    private PlayerStats playerStats;
    Gson gson;

    private int count;
    private int plays = 0;
    private boolean canClick = false;

    int[] backgrounds = {R.drawable.backgroun1, R.drawable.backgroun2, R.drawable.backgroun3,
            R.drawable.backgroun4, R.drawable.backgroun5, R.drawable.backgroun6,
            R.drawable.backgroun7, R.drawable.backgroun8, R.drawable.backgroun9,
            R.drawable.backgroun10, R.drawable.backgroun11, R.drawable.backgroun12,
            R.drawable.backgroun13, R.drawable.backgroun14, R.drawable.backgroun15,
            R.drawable.backgroun16, R.drawable.backgroun17, R.drawable.backgroun18,
            R.drawable.backgroun19, R.drawable.backgroun20, R.drawable.backgroun21,
            R.drawable.backgroun22, R.drawable.backgroun23, R.drawable.backgroun24,
            R.drawable.backgroun25, R.drawable.backgroun26, R.drawable.backgroun27};

    //********************     RUNNABLES     ********************

    Runnable canClickRunnable = new Runnable() {
        @Override
        public void run() {
            mp.start();
            canClick();
        }
    };

    Runnable showNewGameRunnable = new Runnable() {
        @Override
        public void run() {
            showNewGame();
        }
    };

    Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (canClick) {
                        timeCounter.setText(++count + " ms");
                    }
                }
            });
        }
    };

    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mode_classic);

        loadAds();

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        playerStats = getPlayerStats();
        mp = MediaPlayer.create(this, R.raw.gun_sound);

        timeCounter = findViewById(R.id.timeCounter);
        highScoreText = findViewById(R.id.highScore);
        background = findViewById(R.id.mainLayout);

        highScoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHighScore();
            }
        });
        loadHighScore();
        startGameListeners();
    }

    private void startGame() {
        timeCounter.setVisibility(View.INVISIBLE);
        int randomInstant = new Random().nextInt((MAX - MIN) + 1) + MIN;
        count = 0;
        canClick = false;

        playGameListeners();

        handler = new Handler();
        handler.postDelayed(canClickRunnable, randomInstant);
    }

    private void canClick() {
        canClick = true;
        background.setBackgroundColor(getColor(R.color.yellowBackground));
        timeCounter.setText(count + " ms");
        timeCounter.setVisibility(View.VISIBLE);

        scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleAtFixedRate(gameRunnable, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void showNewGame() {
        updatePlayerStats();

        plays++;
        if (plays == 3) {
            plays = 0;
            showFullScreenAdd();
        }

        timeCounter.setVisibility(View.VISIBLE);
        timeCounter.setText("START");
        background.setBackgroundColor(getColor(R.color.white));

        startGameListeners();
    }

    private void newGame() {
        handler = new Handler();
        handler.postDelayed(showNewGameRunnable, 2000);
    }

    private void loadBackGround() {
        if (playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
    }


    //********************     CLICK TESTS     ********************

    private void checkClick() {
        if (canClick)
            correctClick();
        else
            incorrectClick();
    }

    private void correctClick() {
        canClick = false;

        scheduler.shutdown();
        handler.removeCallbacks(gameRunnable);
        future.cancel(true);

        background.setBackgroundColor(getColor(R.color.greenBackgroud));
        timeCounter.setText(count + " ms");
        setHighScore(count);
        updateCorrectPlays();
        updatePlayerScores(count);

        newGame();
    }

    private void incorrectClick() {
        canClick = false;
        background.setBackgroundColor(getColor(R.color.redBackground));
        updateIncorrectPlays();
        newGame();
    }


    //********************     HIGH SCORES     ********************

    private void loadHighScore() {
        if (playerStats.bestReactionTime == Integer.MAX_VALUE) {
            highScoreText.setVisibility(View.INVISIBLE);
            showTutorial();
        } else {
            highScoreText.setVisibility(View.VISIBLE);
            highScoreText.setText(String.format("HighScore: %d ms", playerStats.bestReactionTime));
        }
    }

    private void setHighScore(int newScore) {
        if (playerStats.bestReactionTime == Integer.MAX_VALUE || newScore < playerStats.bestReactionTime)
            playerStats.bestReactionTime = newScore;
        loadHighScore();
    }

    private void shareHighScore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, playerStats.bestReactionTime + " ms is my best reaction time!\n\n" +
                "Download the App at https://play.google.com/store/apps/details?id=com.pack.reactiongame");
        startActivity(Intent.createChooser(shareIntent, "Share text via"));
    }


    //********************     LISTENERS     ********************

    private void startGameListeners() {
        loadBackGround();
        timeCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCounter.setOnClickListener(null);
                startGame();
            }
        });
        background.setOnClickListener(null);
    }

    private void playGameListeners() {
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                checkClick();
            }
        });
        timeCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                checkClick();
            }
        });
    }

    private void endGameListeners() {
        handler.removeCallbacks(canClickRunnable);
        background.setOnClickListener(null);
        timeCounter.setOnClickListener(null);
    }


    //********************     AUX METHODS     ********************

    private void showTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tutorial").
                setMessage("Tap the screen as soon as you hear the sound or see the color." +
                        "\n\nTo share your highScore, tap it." +
                        "\n\nHave fun!")
                .create()
                .show();
    }


    //********************     ACHIEVEMENTS     ********************

    private PlayerStats getPlayerStats() {
        gson = new Gson();
        String json = sharedPref.getString("PlayerStats", null);
        Type type = new TypeToken<PlayerStats>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void updateCorrectPlays() {
        playerStats.correctPlays++;
    }

    private void updateIncorrectPlays() {
        playerStats.wrongPlays++;
    }

    private void updatePlayerScores(int count) {
        if (playerStats.bestReactionTime > count) {
            playerStats.bestReactionTime = count;
        }
        if (playerStats.worstReactionTime < count) {
            playerStats.worstReactionTime = count;
        }
    }

    private void updatePlayerStats() {
        playerStats.checkForAchievements(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        gson = new Gson();
        String json = gson.toJson(playerStats);
        editor.putString("PlayerStats", json);
        editor.apply();
    }


    //********************     ADS METHODS     ********************

    private void showFullScreenAdd() {
        mInterstitialAd.show(ClassicModeActivity.this);
    }

    private void loadAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        // InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, // TEST
        InterstitialAd.load(this, "ca-app-pub-1816824579575646/3791851914", adRequest, // REAL
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
}