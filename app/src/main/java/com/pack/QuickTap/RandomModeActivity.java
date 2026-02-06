package com.pack.QuickTap;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Random;

public class RandomModeActivity extends BaseActivity {

    private static final int MIN = 500;
    private static final int MAX = 2000;

    TextView startButton, highScoreText, currentScoreText;
    ImageView clickButton, backButton;
    MaterialCardView retryButton;
    ConstraintLayout background;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final Gson gson = new Gson();
    private MediaPlayer mp;
    private SettingsManager settingsManager;
    private InterstitialAd mInterstitialAd;

    SharedPreferences sharedPref;
    private PlayerStats playerStats;

    private int availableWidth, availableHeight;
    private int timeInterval = 1000;
    private int clicks, plays = 0;
    private boolean clicked = false;

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

    private final Runnable canClickRunnable = () -> {
        mp.seekTo(0);
        if (settingsManager.isSoundEnabled()) {
            mp.start();
        }
        HapticHelper.vibrateShot(this);
        canClick();
    };

    private final Runnable cantClickRunnable = () -> {
        clickButton.setVisibility(View.INVISIBLE);
        if (!clicked)
            incorrectClick();
        else
            startGame();
    };

    private final Runnable showNewGameRunnable = () -> {
        showNewGame();
        startGameListener();
    };


    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_random);

        settingsManager = new SettingsManager(this);
        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        playerStats = getPlayerStats();

        startButton = findViewById(R.id.startButton);
        highScoreText = findViewById(R.id.highScore);
        currentScoreText = findViewById(R.id.currentScore);
        clickButton = findViewById(R.id.clickButton);
        background = findViewById(R.id.mainLayout);
        backButton = findViewById(R.id.backButton);
        retryButton = findViewById(R.id.retryButton);
        clickButton.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        loadAds();
        mp = MediaPlayer.create(this, R.raw.gun_sound);

        highScoreText.setOnClickListener(v -> shareHighScore());

        final FrameLayout targetArea = findViewById(R.id.targetArea);
        targetArea.post(() -> {
            availableWidth = (int) (targetArea.getWidth() * 0.85);
            availableHeight = (int) (targetArea.getHeight() * 0.85);
        });

        loadHighScore();
        startGameListener();
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

    private void startGame() {
        backButton.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        currentScoreText.setVisibility(View.VISIBLE);
        clicked = false;
        int randomInstant = random.nextInt((MAX - MIN) + 1) + MIN;

        playGameListeners();

        handler.postDelayed(canClickRunnable, randomInstant);
    }

    private void canClick() {
        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) clickButton.getLayoutParams();

        params.leftMargin = random.nextInt(Math.max(1, availableWidth));
        params.topMargin = random.nextInt(Math.max(1, availableHeight));
        clickButton.setLayoutParams(params);

        clickButton.setVisibility(View.VISIBLE);
        animateDotAppear(clickButton);

        handler.postDelayed(cantClickRunnable, timeInterval);
        if (timeInterval > 200)
            timeInterval *= 0.95;
    }

    private void showNewGame() {
        backButton.setVisibility(View.VISIBLE);

        timeInterval = 1000;
        clicks = 0;

        currentScoreText.setVisibility(View.GONE);
        currentScoreText.setText("0");
        startButton.setText("START");
        startButton.setVisibility(View.VISIBLE);
        background.setBackgroundColor(getColor(R.color.colorBackground));
    }

    private void newGame() {
        handler.postDelayed(showNewGameRunnable, 2000);
    }

    private void loadBackGround() {
        if (playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
    }


    //********************     CLICK TESTS     ********************

    private void correctClick() {
        clicks++;
        currentScoreText.setText(String.valueOf(clicks));
        setHighScoreText(clicks);
        endGameListeners();

        clicked = true;
        clickButton.setVisibility(View.INVISIBLE);
        startGame();
    }

    private void incorrectClick() {
        currentScoreText.setText("You lost");
        clicked = false;
        endGameListeners();

        clickButton.setVisibility(View.INVISIBLE);
        background.setBackgroundColor(getColor(R.color.redError));
        backButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);

        updatePlayerStats();
        plays++;
        if (plays == 10) {
            plays = 0;
            showFullScreenAdd();
        }

        retryButton.setOnClickListener(v -> {
            retryButton.setOnClickListener(null);
            retryButton.setVisibility(View.GONE);
            showNewGame();
            startGameListener();
        });
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
                "Download the App at https://play.google.com/store/apps/details?id=com.pack.reactiongame");
        startActivity(Intent.createChooser(shareIntent, "Share text via"));
    }


    //********************     LISTENERS     ********************

    private void playGameListeners() {
        background.setOnClickListener(v -> {
            endGameListeners();
            incorrectClick();
        });
        clickButton.setOnClickListener(v -> {
            endGameListeners();
            correctClick();
        });
    }

    private void startGameListener() {
        loadBackGround();
        startButton.setOnClickListener(v -> {
            startButton.setOnClickListener(null);
            startButton.setVisibility(View.INVISIBLE);
            startGame();
        });
    }

    private void endGameListeners() {
        if (mp != null) mp.pause();
        handler.removeCallbacks(canClickRunnable);
        handler.removeCallbacks(cantClickRunnable);
        background.setOnClickListener(null);
        clickButton.setOnClickListener(null);
    }


    //********************     ANIMATIONS     ********************

    private void animateDotAppear(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        scaleX.setDuration(120);
        scaleY.setDuration(120);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        scaleX.start();
        scaleY.start();
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


    //********************     ADS METHODS     ********************

    private void showFullScreenAdd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(RandomModeActivity.this);
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


    //********************     AUX METHODS     ********************

    private void showTutorial() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tutorial")
                .setMessage("Tap the yellow circle as fast as you can." +
                        "\n\nAfter each tap, the time the circle stays on the screen decreases." +
                        "\n\nTo share your highScore, tap it." +
                        "\n\nHave fun!")
                .setPositiveButton("OK", null)
                .show();
    }
}
