package com.pack.QuickTap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class ClassicModeActivity extends BaseActivity {

    private static final int MIN = 1000;
    private static final int MAX = 5000;
    private static final int DISPLAY_UPDATE_INTERVAL_MS = 16;

    private SharedPreferences sharedPref;
    private SettingsManager settingsManager;
    private TextView timeCounter, highScoreText;
    private ImageView backButton;
    private MaterialCardView retryButton;
    private ConstraintLayout background;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final Gson gson = new Gson();
    private MediaPlayer mp;
    private InterstitialAd mInterstitialAd;
    private PlayerStats playerStats;

    private long startTimeNanos;
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

    private final Runnable canClickRunnable = () -> {
        if (settingsManager.isSoundEnabled()) {
            mp.start();
        }
        HapticHelper.vibrateShot(this);
        canClick();
    };

    private final Runnable showNewGameRunnable = () -> showNewGame();

    private final Runnable displayUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (canClick) {
                int elapsedMs = (int) ((System.nanoTime() - startTimeNanos) / 1_000_000L);
                timeCounter.setText(elapsedMs + " ms");
                handler.postDelayed(this, DISPLAY_UPDATE_INTERVAL_MS);
            }
        }
    };

    //********************     ACTIVITY     ********************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_classic);

        settingsManager = new SettingsManager(this);
        loadAds();

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);
        playerStats = getPlayerStats();
        mp = MediaPlayer.create(this, R.raw.gun_sound);

        timeCounter = findViewById(R.id.timeCounter);
        highScoreText = findViewById(R.id.highScore);
        background = findViewById(R.id.mainLayout);
        backButton = findViewById(R.id.backButton);
        retryButton = findViewById(R.id.retryButton);

        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        highScoreText.setOnClickListener(v -> shareHighScore());
        loadHighScore();
        startGameListeners();
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
        timeCounter.setVisibility(View.INVISIBLE);
        int randomInstant = random.nextInt((MAX - MIN) + 1) + MIN;
        canClick = false;

        playGameListeners();

        handler.postDelayed(canClickRunnable, randomInstant);
    }

    private void canClick() {
        canClick = true;
        startTimeNanos = System.nanoTime();
        background.setBackgroundColor(getColor(R.color.yellowBackground));
        timeCounter.setText("0 ms");
        timeCounter.setVisibility(View.VISIBLE);

        handler.post(displayUpdateRunnable);
    }

    private void showNewGame() {
        backButton.setVisibility(View.VISIBLE);
        timeCounter.setVisibility(View.VISIBLE);
        timeCounter.setText("START");
        background.setBackgroundColor(getColor(R.color.colorBackground));

        startGameListeners();
    }

    private void newGame() {
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
        handler.removeCallbacks(displayUpdateRunnable);

        int elapsedMs = (int) ((System.nanoTime() - startTimeNanos) / 1_000_000L);

        background.setBackgroundColor(getColor(R.color.greenSuccess));
        timeCounter.setText(elapsedMs + " ms");
        animateTapScale(timeCounter);
        setHighScore(elapsedMs);
        updateCorrectPlays();
        updatePlayerScores(elapsedMs);

        updatePlayerStats();
        plays++;
        if (plays == 3) {
            plays = 0;
            showFullScreenAdd();
        }

        newGame();
    }

    private void incorrectClick() {
        canClick = false;
        background.setBackgroundColor(getColor(R.color.redError));
        timeCounter.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        updateIncorrectPlays();

        updatePlayerStats();
        plays++;
        if (plays == 3) {
            plays = 0;
            showFullScreenAdd();
        }

        retryButton.setOnClickListener(v -> {
            retryButton.setOnClickListener(null);
            retryButton.setVisibility(View.GONE);
            showNewGame();
        });
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
        timeCounter.setOnClickListener(v -> {
            timeCounter.setOnClickListener(null);
            startGame();
        });
        background.setOnClickListener(null);
    }

    private void playGameListeners() {
        View.OnClickListener gameClickListener = v -> {
            endGameListeners();
            checkClick();
        };
        background.setOnClickListener(gameClickListener);
        timeCounter.setOnClickListener(gameClickListener);
    }

    private void endGameListeners() {
        handler.removeCallbacks(canClickRunnable);
        background.setOnClickListener(null);
        timeCounter.setOnClickListener(null);
    }


    //********************     ANIMATIONS     ********************

    private void animateTapScale(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(160);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }


    //********************     AUX METHODS     ********************

    private void showTutorial() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tutorial")
                .setMessage("Tap the screen as soon as you hear the sound or see the color." +
                        "\n\nTo share your highScore, tap it." +
                        "\n\nHave fun!")
                .setPositiveButton("OK", null)
                .show();
    }


    //********************     ACHIEVEMENTS     ********************

    private PlayerStats getPlayerStats() {
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
        String json = gson.toJson(playerStats);
        editor.putString("PlayerStats", json);
        editor.apply();
    }


    //********************     ADS METHODS     ********************

    private void showFullScreenAdd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(ClassicModeActivity.this);
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
}
