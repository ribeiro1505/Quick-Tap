package com.example.QuickTap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    private PlayerStats playerStats;

    GridView gridview;
    SharedPreferences sharedPref;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievements);

        loadAds();
        gridview = findViewById(R.id.gridView);
        CustomAdapter customAdapter = new CustomAdapter(images, this);
        gridview.setAdapter(customAdapter);

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);

        playerStats = getPlayerStats();

    }

    private PlayerStats getPlayerStats() {
        gson = new Gson();
        String json = sharedPref.getString("PlayerStats", null);
        Type type = new TypeToken<PlayerStats>() {
        }.getType();
        return gson.fromJson(json, type);
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
                    else
                        new AlertDialog.Builder(AchievementsActivity.this)
                                .setTitle(getString(R.string.achievementUnlocked))
                                .show();
                }
            });

            return view;
        }
    }
}