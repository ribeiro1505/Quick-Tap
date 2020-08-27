package com.example.QuickTap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClassicModeActivity extends AppCompatActivity {

    private static final int MIN = 1;
    private static final int MAX = 10;

    private SharedPreferences sharedPref;
    private TextView timeCounter, highScoreText;
    private ConstraintLayout background;

    ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;

    Handler handler;
    MediaPlayer mp;
    private InterstitialAd mInterstitialAd;

    private int highScore;
    private int count;
    private int plays = 0;
    private boolean canClick = false;

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
        setContentView(R.layout.mode_classic);

        loadAds();

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
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
        handler.postDelayed(canClickRunnable, randomInstant * 1000);
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
        plays++;
        if (plays == 8)
            loadFullScreenAdd();
        else if (plays == 10) {
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

        newGame();
    }

    private void incorrectClick() {
        canClick = false;
        background.setBackgroundColor(getColor(R.color.redBackground));
        newGame();
    }


    //********************     HIGH SCORES     ********************

    private void loadHighScore() {
        highScore = sharedPref.getInt("highScore", -1);

        if (highScore == -1) {
            highScoreText.setVisibility(View.INVISIBLE);
            showTutorial();
        } else {
            highScoreText.setVisibility(View.VISIBLE);
            highScoreText.setText(String.format("HighScore: %d ms", highScore));
        }
    }

    private void setHighScore(int newScore) {
        if (highScore == -1 || newScore < highScore)
            sharedPref.edit().putInt("highScore", newScore).commit();
        loadHighScore();
    }

    private void shareHighScore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, highScore + " ms is my best reaction time!\n\n" +
                "Download the App at https://drive.google.com/drive/folders/1KEt8E7u--aW24HIu11ghAFWDnKlgh2kY?usp=sharing");
        startActivity(Intent.createChooser(shareIntent, "Share text via"));
    }


    //********************     LISTENERS     ********************

    private void startGameListeners() {
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
                setMessage("Tap the screen as soon as you ear the sound or see the color." +
                        "\n\nTo share your highScore, tap it." +
                        "\n\nHave fun!")
                .create()
                .show();
    }


    //********************     ADS METHODS     ********************

    private void loadFullScreenAdd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showFullScreenAdd() {
        mInterstitialAd.show();
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

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
    }
}