package com.pack.QuickTap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AchievementsActivity extends AppCompatActivity {

    int[] images = {R.drawable.plays5, R.drawable.plays50, R.drawable.plays100, R.drawable.plays1000, R.drawable.plays5000, R.drawable.plays100000,
            R.drawable.days5, R.drawable.days10, R.drawable.days100,
            R.drawable.ms500, R.drawable.ms400, R.drawable.ms300, R.drawable.ms200, R.drawable.ms100, R.drawable.ms50,
            R.drawable.random5, R.drawable.random10, R.drawable.random15, R.drawable.random20, R.drawable.random25, R.drawable.random30,
            R.drawable.multiplayer100, R.drawable.multiplayer1000, R.drawable.multiplayer100000,
            R.drawable.snail, R.drawable.wrong100,
            R.drawable.all_achievements};

    int[] greyImages = {R.drawable.plays5grey, R.drawable.plays50grey, R.drawable.plays100grey, R.drawable.plays1000grey, R.drawable.plays5000grey, R.drawable.plays100000grey,
            R.drawable.days5grey, R.drawable.days10grey, R.drawable.days100grey,
            R.drawable.ms500grey, R.drawable.ms400grey, R.drawable.ms300grey, R.drawable.ms200grey, R.drawable.ms100grey, R.drawable.ms50grey,
            R.drawable.random5grey, R.drawable.random10grey, R.drawable.random15grey, R.drawable.random20grey, R.drawable.random25grey, R.drawable.random30grey,
            R.drawable.multiplayer100grey, R.drawable.multiplayer1000grey, R.drawable.multiplayer100000grey,
            R.drawable.misterygrey, R.drawable.misterygrey,
            R.drawable.allgrey};

    int[] backgrounds = {R.drawable.backgroun1, R.drawable.backgroun2, R.drawable.backgroun3,
            R.drawable.backgroun4, R.drawable.backgroun5, R.drawable.backgroun6,
            R.drawable.backgroun7, R.drawable.backgroun8, R.drawable.backgroun9,
            R.drawable.backgroun10, R.drawable.backgroun11, R.drawable.backgroun12,
            R.drawable.backgroun13, R.drawable.backgroun14, R.drawable.backgroun15,
            R.drawable.backgroun16, R.drawable.backgroun17, R.drawable.backgroun18,
            R.drawable.backgroun19, R.drawable.backgroun20, R.drawable.backgroun21,
            R.drawable.backgroun22, R.drawable.backgroun23, R.drawable.backgroun24,
            R.drawable.backgroun25, R.drawable.backgroun26, R.drawable.backgroun27};

    private PlayerStats playerStats;

    GridView gridview;
    ConstraintLayout background;
    SharedPreferences sharedPref;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.achievements);

        loadAds();
        background = findViewById(R.id.layout);
        gridview = findViewById(R.id.gridView);
        CustomAdapter customAdapter = new CustomAdapter(images, this);
        gridview.setAdapter(customAdapter);

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);

        playerStats = getPlayerStats();
        loadBackGround();
    }

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

    private void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadBackGround() {
        if (playerStats.background != -1)
            background.setBackgroundResource(backgrounds[playerStats.background]);
    }

    public class CustomAdapter extends BaseAdapter {

        private int[] images;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(int[] images, Context context) {
            this.images = images;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.row_image, parent, false);
            }

            ImageView imagePhoto = view.findViewById(R.id.imageView);
            if (playerStats.achievements[position])
                imagePhoto.setImageResource(images[position]);
            else
                imagePhoto.setImageResource(greyImages[position]);

            imagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!playerStats.achievements[position])
                        new AlertDialog.Builder(AchievementsActivity.this)
                                .setTitle(getString(R.string.unlockAchievement))
                                .setMessage('\n' + getResources().getStringArray(R.array.achievementsSentences)[position])
                                .show();
                    else {
                        final AlertDialog dialog = new AlertDialog.Builder(AchievementsActivity.this)
                                .setTitle(getString(R.string.achievementUnlocked))
                                .setMessage(getString(R.string.setBackground))
                                .setPositiveButton("Set Background", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        playerStats.background = position;
                                        updatePlayerStats();
                                        loadBackGround();
                                    }
                                })
                                .setNegativeButton("Cancel", null).create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.black));

                            }
                        });
                        dialog.show();
                    }
                }
            });

            return view;
        }
    }
}