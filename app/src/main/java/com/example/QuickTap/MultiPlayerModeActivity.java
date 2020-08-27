package com.example.QuickTap;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MultiPlayerModeActivity extends AppCompatActivity {

    private static final int MIN = 1;
    private static final int MAX = 10;

    TextView topPlayer, bottomPlayer, topPlayerScore, bottomPlayerScore;
    ImageView topBackground, bottomBackground;

    Handler handler;

    MediaPlayer mp;

    private boolean isTopPlayerReady, isBottomPlayerReady = false;
    private boolean canClick = false;

    private int randomInstant;
    private int topPlayerScoreValue, bottomPlayerScoreValue = 0;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_multiplayer);

        mp = MediaPlayer.create(this, R.raw.gun_sound);

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
        handler.postDelayed(canClickRunnable, randomInstant * 1000);

    }

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

    private void canClick() {
        canClick = true;
        topBackground.setBackgroundColor(getColor(R.color.yellowBackground));
        bottomBackground.setBackgroundColor(getColor(R.color.yellowBackground));
    }

    private void checkClick(String player) {
        handler.removeCallbacks(canClickRunnable);

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

    private void newGame() {
        topPlayerScore.setText(String.valueOf(topPlayerScoreValue));
        bottomPlayerScore.setText(String.valueOf(bottomPlayerScoreValue));

        handler = new Handler();
        handler.postDelayed(showNewGameRunnable, 2000);
    }

    private void showNewGame() {
        topPlayer.setVisibility(View.VISIBLE);
        bottomPlayer.setVisibility(View.VISIBLE);
        topBackground.setBackgroundColor(getColor(R.color.white));
        bottomBackground.setBackgroundColor(getColor(R.color.white));
        readyToPlay();
    }

    private void endGameListeners() {
        topBackground.setOnClickListener(null);
        bottomBackground.setOnClickListener(null);
    }

}
