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
import com.ecolife.activities.ActivityMealsMealsOfDay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


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
     * Convert given double to string
     * @param value
     * @return
     */
    private String convertDataToDoubleText(double value) {
        if (value % 1 != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#####.##");
            return decimalFormat.format(value);
        } else {
            return String.valueOf((int) value);
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

        // Get date from arguments
        if (getArguments().containsKey("date")) {
            date = getArguments().getString("date");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = formatter.format(new Date());
        }

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        // Get today nutrients from databases
        Cursor cursorDataFood = ((ActivityMain) requireContext())
                .databaseHelper
                .getConsumedMealsSums(date.format(now));

        // Store data to class variable "dataFood", if no data, fallback with 0
        if (cursorDataFood.getCount() > 0) {
            cursorDataFood.moveToFirst();
            dataFood = new double[6];
            for (int i = 0; i < 6; i++) {
                dataFood[i] = cursorDataFood.getDouble(i);
            }
        } else {
            dataFood = new double[] {0, 0, 0, 0, 0, 0, 0};
        }
        cursorDataFood.close();


        // Get goals from database, fallback with 1
        Cursor cursorSettings = ((ActivityMain) requireContext()).databaseHelper.getSettingsGoals();
        if (cursorSettings.getCount() > 0) {
            cursorSettings.moveToFirst();
            dataGoals = new double[] {
                    cursorSettings.getDouble(0),  // Goal Calories
                    cursorSettings.getDouble(1),  // Goal Fat
                    cursorSettings.getDouble(2),  // Goal Carbohydrates
                    cursorSettings.getDouble(3)  // Goal Protein
            };
        } else {
            dataGoals = new double[] {1, 1, 1, 1};
        }
        cursorSettings.close();

    }

    /**
     * Inflate the layout for this fragment
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
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Dashboard view
        ProgressBar progressBarMain = getView().findViewById(R.id.progressBarDBNCaloriesMain);
        TextView textProgressMain = getView().findViewById(R.id.textViewDNCaloriesMain);
        ObjectAnimator.ofInt(progressBarMain, "progress", percentOf(dataFood[0], dataGoals[0])).start();
        textProgressMain.setText(convertDataToIntText(dataFood[0]));

        ProgressBar progressBarFat = getView().findViewById(R.id.progressBarDBNFatMain);
        TextView textProgressFat = getView().findViewById(R.id.textViewProgressDBNFatMain);
        ObjectAnimator.ofInt(progressBarFat, "progress", percentOf(dataFood[1], dataGoals[1])).start();
        String textFat = convertDataToIntText(dataFood[1]) + " g";
        textProgressFat.setText(textFat);

        ProgressBar progressBarCarbohydrates = getView().findViewById(R.id.progressBarDBNCarbsMain);
        TextView textProgressCarbohydrates = getView().findViewById(R.id.textViewProgressCarbohydrates);
        ObjectAnimator.ofInt(progressBarCarbohydrates, "progress", percentOf(dataFood[3], dataGoals[2])).start();
        String textCarbohydrates = convertDataToIntText(dataFood[3]) + " g";
        textProgressCarbohydrates.setText(textCarbohydrates);

        ProgressBar progressBarProtein = getView().findViewById(R.id.progressBarDBNProteinMain);
        TextView textProgressProtein = getView().findViewById(R.id.textViewProgressDBNProteinMain);
        ObjectAnimator.ofInt(progressBarProtein, "progress", percentOf(dataFood[5], dataGoals[3])).start();
        String textProtein = convertDataToIntText(dataFood[5]) + " g";
        textProgressProtein.setText(textProtein);


        // Table view
        int[] tableViews = {
                R.id.textViewDBNDetailsCalories,
                R.id.textViewDBNDetailsFat,
                R.id.textViewDBNDetailsFatSat,
                R.id.textViewDBNDetailsCarbs,
                R.id.textViewDBNDetailsSugar,
                R.id.textViewDBNDetailsProtein
        };

        for (int i = 0; i < tableViews.length; i++) {
            TextView textViewDetailsCal = getView().findViewById(tableViews[i]);
            textViewDetailsCal.setText(convertDataToDoubleText(dataFood[i]));
        }

        // Initialize button view today view
        Button buttonShowEatenMeals = getView().findViewById(R.id.buttonEatenMeals);
        buttonShowEatenMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityMealsMealsOfDay.class);
                intent.putExtra("date", date);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  // Start activity without animation
                startActivity(intent);
            }
        });
    }

}