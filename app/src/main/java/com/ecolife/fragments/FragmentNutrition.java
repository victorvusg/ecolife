package com.ecolife.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecolife.ActivityMain;
import com.ecolife.R;
import com.ecolife.activities.ActivityMealsAddDailyEntry;
import com.ecolife.activities.ActivityMealsOfDay;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class is fragment for Nutrition
 */
public class FragmentNutrition extends Fragment {

    private String date;
    private double[] dataFood;
    private double[] dataGoals;

    /**
     * This method uses to calculate percentage of a value
     * @param current Current value to calculate percentage
     * @param max Maximum value of value
     * @return integer value of percentage
     */
    private int percentOf(double current, double max) {
        // If max is equal to 0, a invalid value then return 0
        if (max == 0) return 0;
        return (int) ((current / max) * 100);
    }

    /**
     *
     * @param value
     * @return
     */
    private String convertDataToDoubleText(double value) {
        // Convert given double to string.
        if (value % 1 == 0) {
            // -> Value has only .0 decimals. Cut it out by converting to int.
            return String.valueOf((int) value);
        } else {
            // -> Value has decimals. Round up to 2 decimal-digits.
            DecimalFormat df = new DecimalFormat("#####.##");
            return String.valueOf(df.format(value));
        }
    }

    /**
     * This method uses to convert a floating number to rounded string number
     * @param value
     * @return
     */
    private String convertDataToIntText(double value) {
        return String.valueOf((int) value);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        // Load data from database
        Cursor cursorDataFood = ((ActivityMain) requireContext())
                .databaseHelper
                .getConsumedMealsSums(date.format(now));

        if (cursorDataFood.getCount() > 0) {
            cursorDataFood.moveToFirst();
            dataFood = new double[5];
            for (int i = 0; i < 5; i++) {
                dataFood[i] = cursorDataFood.getDouble(i);
            }

        } else {
            dataFood = new double[] {0, 0, 0, 0, 0};
        }
        cursorDataFood.close();

        Cursor cursorSettings = ((ActivityMain) requireContext()).databaseHelper.getSettingsGoals();
        if (cursorSettings.getCount() > 0) {
            cursorSettings.moveToFirst();
            dataGoals = new double[] {
                    cursorSettings.getDouble(0),  // Goal Calories
                    cursorSettings.getDouble(1),  // Goal Protein
                    cursorSettings.getDouble(2),  // Goal Sat Fat
                    cursorSettings.getDouble(3),  // Goal Carbohydrates
                    cursorSettings.getDouble(4),  // Goal Water
            };
        } else {
            dataGoals = new double[] {2000, 2000, 2000, 2000, 2000, 2000};
        }
        cursorSettings.close();

    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrition, container, false);
    }

    /**
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        /**
         * Set values for main-dashboard
         */

        int[] progressViews = {
                R.id.progressBarCalories,
                R.id.progressBarProtein,
                R.id.progressBarSatFat,
                R.id.progressBarWater
        };

        int[] textViews = {
                R.id.textViewCalories,
                R.id.textViewProgressProtein,
                R.id.textViewProgressSatFat,
                R.id.textViewProgressWater
        };

        for (int i = 0; i < 4; i++) {
            ProgressBar proBar = getView().findViewById(progressViews[i]);
            TextView text = getView().findViewById(textViews[i]);
            ObjectAnimator.ofInt(proBar, "progress", percentOf(dataFood[i], dataGoals[i])).start();
            String valueWithUnit = convertDataToIntText(dataFood[i]) + " g";
            text.setText(valueWithUnit);

        }

        /**
         * Today meals button
         */
        Button buttonShowEatenMeals = getView().findViewById(R.id.buttonEatenMeals);
        buttonShowEatenMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityMealsOfDay.class);
                intent.putExtra("date", date);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  // Start activity without animation
                startActivity(intent);
            }
        });
    }

}