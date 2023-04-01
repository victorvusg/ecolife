package com.ecolife.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecolife.ActivityMain;
import com.ecolife.R;



public class FragmentSettings extends Fragment {

    private double[] dataGoals;
    private String currentLanguage;
    private boolean savePossible = false;
    private boolean firstSelect = true;

    private Button saveButton;

    private class textWatcher implements TextWatcher {
        private int id;
        private textWatcher(int id) {
            this.id = id;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // pass
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // pass
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Update value
            dataGoals[id] = Double.parseDouble(editable.toString());

            // Update background resource of save button
            enableSaveButton();
        }
    }

    private String convertDataToText(double value) {
        // Convert given double to string.
        // Check if double value has ".0" decimals. If yes cut it out.
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(value);
        }
    }

    private void enableSaveButton() {
        saveButton.setBackgroundResource(R.drawable.shape_box_round_pop);
        saveButton.setTextColor(getContext().getColor(R.color.text_high));
        saveButton.setVisibility(View.VISIBLE);
        savePossible = true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load data from database
        Cursor cursorGoals = ((ActivityMain) requireContext()).databaseHelper.getSettingsGoals();
        if (cursorGoals.getCount() > 0) {
            cursorGoals.moveToFirst();
            dataGoals = new double[] {
                    cursorGoals.getDouble(0),
                    cursorGoals.getDouble(1),
                    cursorGoals.getDouble(2),
                    cursorGoals.getDouble(3),
                    cursorGoals.getDouble(4),
                    cursorGoals.getDouble(5)
            };
        } else {
            dataGoals = new double[] {0, 0, 0, 0, 0, 0};
        }
        cursorGoals.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Nutrition goal settings
        EditText editTextCalories = getView().findViewById(R.id.editTextSettingsGoalsCal);
        editTextCalories.setText(convertDataToText(dataGoals[0]));
        editTextCalories.addTextChangedListener(new textWatcher(0));

        EditText editTextProtein = getView().findViewById(R.id.editTextSettingsGoalsProtein);
        editTextProtein.setText(convertDataToText(dataGoals[1]));
        editTextProtein.addTextChangedListener(new textWatcher(1));

        EditText editTextFat = getView().findViewById(R.id.editTextSettingsGoalsFat);
        editTextFat.setText(convertDataToText(dataGoals[2]));
        editTextFat.addTextChangedListener(new textWatcher(2));

        EditText editTextSatFat = getView().findViewById(R.id.editTextSettingsGoalsSatFat);
        editTextSatFat.setText(convertDataToText(dataGoals[3]));
        editTextSatFat.addTextChangedListener(new textWatcher(3));

        EditText editTextCarbs = getView().findViewById(R.id.editTextSettingsGoalsCarbs);
        editTextCarbs.setText(convertDataToText(dataGoals[4]));
        editTextCarbs.addTextChangedListener(new textWatcher(4));

        EditText editWater = getView().findViewById(R.id.editTextSettingsGoalsWater);
        editWater.setText(convertDataToText(dataGoals[5]));
        editWater.addTextChangedListener(new textWatcher(5));


        // Button
        saveButton = getView().findViewById(R.id.buttonSaveSettings);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savePossible) {
                    savePossible = false;
                    saveButton.setBackgroundResource(R.drawable.shape_box_round_middle);
                    saveButton.setTextColor(getContext().getColor(R.color.text_middle));
                    saveButton.setVisibility(View.INVISIBLE);

                    ((ActivityMain) requireContext()).databaseHelper.setSettingsGoals(dataGoals);

                }
            }
        });
    }

}