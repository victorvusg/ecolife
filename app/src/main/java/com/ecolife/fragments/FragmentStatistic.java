package com.ecolife.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecolife.ActivityMain;
import com.ecolife.activities.Activity_Calendar;
import com.ecolife.R;
import com.ecolife.activities.Activity_Meals_AddDailyEntry;
import com.ecolife.activities.Activity_Meals_MealsOfDay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


/**
 * This class is fragment for Nutrition
 */
public class FragmentStatistic extends Fragment {

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

        // Get date from arguments
        if (getArguments().containsKey("date")) {
            date = getArguments().getString("date");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = formatter.format(new Date());
        }

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        // Load data from database
        Cursor cursorDataFood = ((ActivityMain) requireContext())
                .databaseHelper
                .getConsumedMealsSums(date.format(now));
        if (cursorDataFood.getCount() > 0) {
            cursorDataFood.moveToFirst();
            dataFood = new double[31];
            for (int i = 0; i <= 30; i++) {
                dataFood[i] = cursorDataFood.getDouble(i);
            }

        } else {
            dataFood = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        cursorDataFood.close();

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
            dataGoals = new double[] {2000, 2000, 2000, 2000};
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Date
        TextView textDate = view.findViewById(R.id.textViewDBNDate);
        textDate.setText(date);

        // Set values for details-dashboard
        TextView textViewDetailsCal = getView().findViewById(R.id.textViewDBNDetailsCalories);
        textViewDetailsCal.setText(convertDataToDoubleText(dataFood[0]));

        TextView textViewDetailsFat = getView().findViewById(R.id.textViewDBNDetailsFat);
        textViewDetailsFat.setText(convertDataToDoubleText(dataFood[1]));

        TextView textViewDetailsFatSat = getView().findViewById(R.id.textViewDBNDetailsFatSat);
        textViewDetailsFatSat.setText(convertDataToDoubleText(dataFood[2]));

        TextView textViewDetailsCarbs = getView().findViewById(R.id.textViewDBNDetailsCarbs);
        textViewDetailsCarbs.setText(convertDataToDoubleText(dataFood[3]));

        TextView textViewDetailsSugar = getView().findViewById(R.id.textViewDBNDetailsSugar);
        textViewDetailsSugar.setText(convertDataToDoubleText(dataFood[4]));

        TextView textViewDetailsProtein = getView().findViewById(R.id.textViewDBNDetailsProtein);
        textViewDetailsProtein.setText(convertDataToDoubleText(dataFood[5]));


        ImageButton buttonCalendar = getView().findViewById(R.id.buttonDBNCalendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Activity_Calendar.class);
                intent.putExtra("date", date);
                intent.putExtra("fragmentID", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  // Start activity without animation
                startActivity(intent);
            }
        });
    }

}