package com.example.QuickTap;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

    private boolean[] achievements;

    SharedPreferences sharedPref;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievements);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPref = getSharedPreferences("GameFile", MODE_PRIVATE);

        achievements = getAchievements();

    }

    private boolean[] getAchievements() {
        gson = new Gson();
        String json = sharedPref.getString("achievements", null);
        Type type = new TypeToken<boolean[]>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}

/*Share your highscore
1 jogada correta
5 jogadas corretas
50 jogadas corretas
100 jogadas corretas
1000 jogadas corretas
5000 jogadas corretas
100000 jogadas corretas
5 dias consecutivos a jogar
10 dias consecutivos a jogar
100 dias consecutivos a jogar
365 dias consecutivos a jogar
Menos de 500 ms de reação
Menos de 400 ms de reação
Menos de 300 ms de reação
Menos de 200 ms de reação
Menos de 100 ms de reação
Menos de 50 ms de reação
Menos de 10 ms de reação
Todos os objectivos completados!
Jogar 100 jogos no modo Multi-player
Jogar 1000 jogos no modo Multi-player
Objectivo surpresa (mais de 1000000 ms de reação)
Objectivo surpresa (100 jogadas erradas)*/