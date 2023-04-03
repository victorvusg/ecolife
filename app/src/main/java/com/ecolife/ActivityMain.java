package com.ecolife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.ecolife.fragments.EcoLifeFragmentNutrition;

import com.ecolife.database.SQLiteDatabase;
import com.ecolife.fragments.EcoLifeFragmentSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity {

    public String date;
    private int currentFragmentID = 0;
    public SQLiteDatabase SQLiteDatabase;


    private void setFragmentFood(String date) {
        EcoLifeFragmentNutrition fragment = new EcoLifeFragmentNutrition();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void setFragmentSettings() {
        EcoLifeFragmentSettings fragment = new EcoLifeFragmentSettings();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecoapp_activity_main);

        // Database
        SQLiteDatabase = new SQLiteDatabase(ActivityMain.this);

        // Get data if activity was started by another activity
        Intent intent = getIntent();

        // Get current date
        if (getIntent().hasExtra("date")) {
            date = intent.getStringExtra("date");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = formatter.format(new Date());
        }

        // If there is a fragmentID submitted take it. Else keep previously set or default one (=0)
        if (getIntent().hasExtra("fragmentID")) {
            currentFragmentID = intent.getIntExtra("fragmentID", 0);
        }

        // Set current fragment based on fragmentID
        switch (currentFragmentID) {
            case 0:
                setFragmentFood(date);
                break;
            case 1:
                setFragmentSettings();
                break;
            default:
                break;
        }

        /**
         * Set up nav bar
         */
        BottomNavigationView navBar = findViewById(R.id.bottom_navigation);
        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_bar_home:
                        if (currentFragmentID != 0) {
                            setFragmentFood(date);
                            currentFragmentID = 0;
                        }
                        return true;
                    case R.id.nav_bar_settings:
                        if (currentFragmentID != 1) {
                            setFragmentSettings();
                            currentFragmentID = 1;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        SQLiteDatabase.close();
        super.onDestroy();
    }

}