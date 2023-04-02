package com.ecolife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import com.ecolife.fragments.FragmentNutrition;

import com.ecolife.data.DatabaseHelper;
import com.ecolife.fragments.FragmentSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity {

    public String date;
    private int currentFragmentID = 0;
    public DatabaseHelper databaseHelper;


    private void setFragmentFood(String date) {
        FragmentNutrition fragment = new FragmentNutrition();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void setFragmentSettings() {
        FragmentSettings fragment = new FragmentSettings();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database
        databaseHelper = new DatabaseHelper(ActivityMain.this);

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

        // -----------------------------------------------------------------------------------------
        // Setup navigation bar
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
        databaseHelper.close();
        super.onDestroy();
    }

}