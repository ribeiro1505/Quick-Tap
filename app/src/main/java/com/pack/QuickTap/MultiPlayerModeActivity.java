package com.pack.QuickTap;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MultiPlayerModeActivity extends AppCompatActivity {

    private static final int MIN = 1000;
    private static final int MAX = 5000;

    TextView topPlayer, bottomPlayer, topPlayerScore, bottomPlayerScore;
    ImageView topBackground, bottomBackground;
    ConstraintLayout background;

    Handler handler;
    MediaPlayer mp;

    SharedPreferences sharedPref;
    private PlayerStats playerStats;
    Gson gson;
    private InterstitialAd mInterstitialAd;

    private boolean isTopPlayerReady, isBottomPlayerReady = false;
    private boolean canClick = false;

    private int randomInstant;
    private int topPlayerScoreValue, bottomPlayerScoreValue, plays = 0;

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

    Runnable showNewGameRunnable = new Runnable() {
        @Override
        public void run() {
            showNewGame();
        }
    };

    Runnable canClickRunnable = new Runnable() {
        @Override
        public void run() {
            mp.start();
            canClick();
        }
    };


    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mode_multiplayer);

        loadAds();

        background = findViewById(R.id.layout);
        mp = MediaPlayer.create(this, R.raw.gun_sound);
        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        playerStats = getPlayerStats();
        loadBackGround();

        topPlayer = findViewById(R.id.topPlayer);
        topBackground = findViewById(R.id.topBackground);
        bottomPlayer = findViewById(R.id.bottomPlayer);
        bottomBackground = findViewById(R.id.bottomBackground);
        topPlayerScore = findViewById(R.id.topPlayerScore);
        bottomPlayerScore = findViewById(R.id.bottomPlayerScore);

        topPlayerScore.setText(String.valueOf(topPlayerScoreValue));
        bottomPlayerScore.setText(String.valueOf(bottomPlayerScoreValue));

        readyToPlay();
    }

    private void newGame() {
        topPlayerScore.setText(String.valueOf(topPlayerScoreValue));
        bottomPlayerScore.setText(String.valueOf(bottomPlayerScoreValue));

        handler = new Handler();
        handler.postDelayed(showNewGameRunnable, 2000);
    }

    private void loadBackGround() {
        if (playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
    }

    private void showNewGame() {
        updatePlayerStats();

        plays++;
        if (plays == 3) {
            plays = 0;
            showFullScreenAdd();
        }

        topPlayer.setVisibility(View.VISIBLE);
        bottomPlayer.setVisibility(View.VISIBLE);
        topBackground.setBackgroundColor(0x00000000);
        bottomBackground.setBackgroundColor(0x00000000);
        readyToPlay();
    }

    private void readyToPlay() {
        topPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topPlayer.setOnClickListener(null);
                topPlayer.setVisibility(View.INVISIBLE);
                isTopPlayerReady = true;
                if (isBottomPlayerReady)
                    startGame();
            }
        });

        bottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPlayer.setOnClickListener(null);
                bottomPlayer.setVisibility(View.INVISIBLE);
                isBottomPlayerReady = true;
                if (isTopPlayerReady)
                    startGame();
            }
        });
    }

    private void startGame() {
        isBottomPlayerReady = false;
        isTopPlayerReady = false;

        randomInstant = new Random().nextInt((MAX - MIN) + 1) + MIN;
        canClick = false;

        playGameListeners();

        handler = new Handler();
        handler.postDelayed(canClickRunnable, randomInstant);
    }

    private void canClick() {
        canClick = true;
        topBackground.setBackgroundColor(getColor(R.color.yellowBackground));
        bottomBackground.setBackgroundColor(getColor(R.color.yellowBackground));
    }


    //********************     LISTENERS     ********************

    private void playGameListeners() {
        topBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                checkClick("top");
            }
        });

        bottomBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameListeners();
                checkClick("bottom");
            }
        });
    }

    private void endGameListeners() {
        topBackground.setOnClickListener(null);
        bottomBackground.setOnClickListener(null);
    }


    //********************     CLICK TESTS     ********************

    private void checkClick(String player) {
        handler.removeCallbacks(canClickRunnable);
        playerStats.multiPlayerGames++;

        if (canClick)
            correctClick(player);
        else
            incorrectClick(player);
    }

    private void incorrectClick(String player) {
        if (player.equals("top"))
            BottomWins();
        else
            TopWins();
        newGame();
    }

    private void correctClick(String player) {
        if (player.equals("top"))
            TopWins();
        else
            BottomWins();
        newGame();
    }


    //********************     WINNER     ********************

    private void TopWins() {
        topBackground.setBackgroundColor(getColor(R.color.greenBackgroud));
        bottomBackground.setBackgroundColor(getColor(R.color.redBackground));
        topPlayerScoreValue++;
    }

    private void BottomWins() {
        bottomBackground.setBackgroundColor(getColor(R.color.greenBackgroud));
        topBackground.setBackgroundColor(getColor(R.color.redBackground));
        bottomPlayerScoreValue++;
    }


    //********************     ADS     ********************

    private void showFullScreenAdd() {
        mInterstitialAd.show(MultiPlayerModeActivity.this);
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

}
