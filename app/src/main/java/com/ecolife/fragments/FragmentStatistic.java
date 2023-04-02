package com.ecolife.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecolife.ActivityMain;
import com.ecolife.activities.ActivityCalendar;
import com.ecolife.R;

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
                    cursorSettings.getDouble(3), // Goal Carbohydrates
                    cursorSettings.getDouble(4),  // Goal Water
            };
        } else {
            dataGoals = new double[] {2000, 2000, 2000, 2000, 2000};
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

        int[] views = {
                R.id.textViewDetailsCalories,
                R.id.textViewDetailsProtein,
                R.id.textViewDetailsFatSat,
                R.id.textViewDetailsCarbs,
                R.id.textViewDetailsWater
        };

        for (int i = 0; i < views.length; i++) {
            TextView tw = getView().findViewById(i);
            tw.setText(convertDataToDoubleText(dataFood[i]));
        }

        ImageButton buttonCalendar = getView().findViewById(R.id.buttonCalendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityCalendar.class);
                intent.putExtra("date", date);
                intent.putExtra("fragmentID", 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

}