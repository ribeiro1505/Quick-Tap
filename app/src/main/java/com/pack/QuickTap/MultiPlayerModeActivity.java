package com.pack.QuickTap;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Random;

public class MultiPlayerModeActivity extends BaseActivity {

    private static final int MIN = 1000;
    private static final int MAX = 5000;

    TextView topPlayer, bottomPlayer, topPlayerScore, bottomPlayerScore;
    ImageView topBackground, bottomBackground, backButton;
    ConstraintLayout background;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final Gson gson = new Gson();
    private MediaPlayer mp;
    private SettingsManager settingsManager;

    SharedPreferences sharedPref;
    private PlayerStats playerStats;
    private InterstitialAd mInterstitialAd;

    private boolean isTopPlayerReady, isBottomPlayerReady = false;
    private boolean canClick = false;

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

    private final Runnable showNewGameRunnable = () -> showNewGame();

    private final Runnable canClickRunnable = () -> {
        if (settingsManager.isSoundEnabled()) {
            mp.start();
        }
        HapticHelper.vibrateShot(this);
        canClick();
    };


    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_multiplayer);

        settingsManager = new SettingsManager(this);
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
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        topPlayerScore.setText(String.valueOf(topPlayerScoreValue));
        bottomPlayerScore.setText(String.valueOf(bottomPlayerScoreValue));

        readyToPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    private void newGame() {
        topPlayerScore.setText(String.valueOf(topPlayerScoreValue));
        bottomPlayerScore.setText(String.valueOf(bottomPlayerScoreValue));

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

        backButton.setVisibility(View.VISIBLE);
        topPlayer.setVisibility(View.VISIBLE);
        bottomPlayer.setVisibility(View.VISIBLE);
        topBackground.setBackgroundColor(0x00000000);
        bottomBackground.setBackgroundColor(0x00000000);
        readyToPlay();
    }

    private void readyToPlay() {
        topPlayer.setOnClickListener(v -> {
            topPlayer.setOnClickListener(null);
            topPlayer.setVisibility(View.INVISIBLE);
            isTopPlayerReady = true;
            if (isBottomPlayerReady)
                startGame();
        });

        bottomPlayer.setOnClickListener(v -> {
            bottomPlayer.setOnClickListener(null);
            bottomPlayer.setVisibility(View.INVISIBLE);
            isBottomPlayerReady = true;
            if (isTopPlayerReady)
                startGame();
        });
    }

    private void startGame() {
        backButton.setVisibility(View.GONE);
        isBottomPlayerReady = false;
        isTopPlayerReady = false;

        int randomInstant = random.nextInt((MAX - MIN) + 1) + MIN;
        canClick = false;

        playGameListeners();

        handler.postDelayed(canClickRunnable, randomInstant);
    }

    private void canClick() {
        canClick = true;
        topBackground.setBackgroundColor(getColor(R.color.yellowBackground));
        bottomBackground.setBackgroundColor(getColor(R.color.yellowBackground));
    }


    //********************     LISTENERS     ********************

    private void playGameListeners() {
        topBackground.setOnClickListener(v -> {
            endGameListeners();
            checkClick("top");
        });

        bottomBackground.setOnClickListener(v -> {
            endGameListeners();
            checkClick("bottom");
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
        topBackground.setBackgroundColor(getColor(R.color.greenSuccess));
        bottomBackground.setBackgroundColor(getColor(R.color.redError));
        topPlayerScoreValue++;
    }

    private void BottomWins() {
        bottomBackground.setBackgroundColor(getColor(R.color.greenSuccess));
        topBackground.setBackgroundColor(getColor(R.color.redError));
        bottomPlayerScoreValue++;
    }


    //********************     ADS     ********************

    private void showFullScreenAdd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MultiPlayerModeActivity.this);
        }
    }

    private void loadAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.ad_interstitial_id), adRequest,
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
        String json = sharedPref.getString("PlayerStats", null);
        Type type = new TypeToken<PlayerStats>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void updatePlayerStats() {
        playerStats.checkForAchievements(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = gson.toJson(playerStats);
        editor.putString("PlayerStats", json);
        editor.apply();
    }
}
