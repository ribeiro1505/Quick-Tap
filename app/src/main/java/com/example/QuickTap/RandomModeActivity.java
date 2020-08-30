package com.example.QuickTap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Random;

public class RandomModeActivity extends AppCompatActivity {

    private static final int MIN = 1;
    private static final int MAX = 2;

    TextView startButton, highScoreText;
    ImageView clickButton;
    ConstraintLayout background;

    Handler handler;
    MediaPlayer mp;
    private InterstitialAd mInterstitialAd;

    SharedPreferences sharedPref;
    private PlayerStats playerStats;
    Gson gson;

    private int timeInterval = 1000;
    private int clicks, plays = 0;
    private boolean clicked = false;


    //********************     RUNNABLES     ********************

    Runnable canClickRunnable = new Runnable() {
        @Override
        public void run() {
            mp.seekTo(0);
            mp.start();
            canClick();
        }
    };

    Runnable cantClickRunnable = new Runnable() {
        @Override
        public void run() {
            clickButton.setVisibility(View.INVISIBLE);
            if (!clicked)
                incorrectClick();
            else
                startGame();
        }
    };

    Runnable showNewGameRunnable = new Runnable() {
        @Override
        public void run() {
            showNewGame();
            startGameListener();
        }
    };


    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_random);

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        playerStats = getPlayerStats();

        startButton = findViewById(R.id.startButton);
        highScoreText = findViewById(R.id.highScore);
        clickButton = findViewById(R.id.clickButton);
        background = findViewById(R.id.mainLayout);
        clickButton.setVisibility(View.INVISIBLE);

        loadAds();
        mp = MediaPlayer.create(this, R.raw.gun_sound);

        highScoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHighScore();
            }
        });

        loadHighScore();
        startGameListener();
    }

    private void startGame() {
        clicked = false;
        int randomInstant = new Random().nextInt((MAX - MIN) + 1) + MIN;

        playGameListeners();

        handler = new Handler();
        handler.postDelayed(canClickRunnable, randomInstant * 1000);
    }

    private void canClick() {
        AbsoluteLayout.LayoutParams absParams =
                (AbsoluteLayout.LayoutParams) clickButton.getLayoutParams();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.7);
        int height = (int) (displaymetrics.heightPixels * 0.6);

        Random r = new Random();

        absParams.x = r.nextInt(width);
        absParams.y = r.nextInt(height);
        clickButton.setLayoutParams(absParams);

        clickButton.setVisibility(View.VISIBLE);

        handler = new Handler();
        handler.postDelayed(cantClickRunnable, timeInterval);
        if (timeInterval > 200)
            timeInterval *= 0.95;
    }

    private void showNewGame() {
        updatePlayerStats();

        timeInterval = 1000;
        clicks = 0;

        plays++;
        if (plays == 3)
            loadFullScreenAdd();
        else if (plays == 5) {
            plays = 0;
            showFullScreenAdd();
        }

        startButton.setVisibility(View.VISIBLE);
        background.setBackgroundColor(getColor(R.color.white));
    }

    private void newGame() {
        handler = new Handler();
        handler.postDelayed(showNewGameRunnable, 2000);
    }


    //********************     CLICK TESTS     ********************

    private void correctClick() {
        clicks++;
        setHighScoreText(clicks);
        endGameListeners();

        clicked = true;
        clickButton.setVisibility(View.INVISIBLE);
        startGame();
    }

    private void incorrectClick() {
        clicked = false;
        endGameListeners();

        clickButton.setVisibility(View.INVISIBLE);
        background.setBackgroundColor(getColor(R.color.redBackground));
        newGame();
    }


    //********************     HIGH SCORES     ********************

    private void setHighScoreText(int newScore) {
        if (playerStats.randomClicks == 0 || newScore > playerStats.randomClicks)
            playerStats.randomClicks = newScore;
        loadHighScore();
    }

    private void loadHighScore() {
        if (playerStats.randomClicks == 0) {
            highScoreText.setVisibility(View.INVISIBLE);
            showTutorial();
        } else {
            highScoreText.setText("HighScore: " + playerStats.randomClicks);
            highScoreText.setVisibility(View.VISIBLE);
        }
    }

    private void shareHighScore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, playerStats.randomClicks + " clicks is my best score in random mode!\n\n" +
                "Download the App at https://drive.google.com/drive/folders/1KEt8E7u--aW24HIu11ghAFWDnKlgh2kY?usp=sharing");
        startActivity(Intent.createChooser(shareIntent, "Share text via"));
    }


    //********************     LISTENERS     ********************

    private void playGameListeners() {
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                incorrectClick();
            }
        });
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                correctClick();
            }
        });
    }

    private void startGameListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setOnClickListener(null);
                startButton.setVisibility(View.INVISIBLE);
                startGame();
            }
        });
    }

    private void endGameListeners() {
        mp.pause();
        handler.removeCallbacks(canClickRunnable);
        handler.removeCallbacks(cantClickRunnable);
        background.setOnClickListener(null);
        clickButton.setOnClickListener(null);
    }


    //********************     ACHIEVEMENTS     ********************

    private PlayerStats getPlayerStats() {
        gson = new Gson();
        String json = sharedPref.getString("PlayerStats", null);
        Type type = new TypeToken<PlayerStats>() {
        }.getType();
        return gson.fromJson(json, type);
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

    private void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
    }

    private void loadFullScreenAdd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showFullScreenAdd() {
        mInterstitialAd.show();
    }


    //********************     AUX METHODS     ********************

    private void showTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tutorial").
                setMessage("Tap the yellow circle as fast as you can." +
                        "\n\nAfter each tap, the time the circle stays on the screen decreases." +
                        "\n\nTo share your highScore, tap it." +
                        "\n\nHave fun!")
                .create()
                .show();
    }

}