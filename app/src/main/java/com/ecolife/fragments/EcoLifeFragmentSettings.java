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
import com.ecolife.utils.Common;

public class EcoLifeFragmentSettings extends Fragment {

    private double[] dataGoals;
    private boolean savePossible = false;

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

    private void enableSaveButton() {
        saveButton.setBackgroundResource(R.drawable.ecoapp_shape_box_round_pop);
        saveButton.setTextColor(getContext().getColor(R.color.text_high));
        saveButton.setVisibility(View.VISIBLE);
        savePossible = true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load data from database
        Cursor cursorGoals = ((ActivityMain) requireContext()).SQLiteDatabase.getSettingsGoals();
        if (cursorGoals.getCount() > 0) {
            cursorGoals.moveToFirst();
            dataGoals = new double[] {
                    cursorGoals.getDouble(0),
                    cursorGoals.getDouble(1),
                    cursorGoals.getDouble(2),
                    cursorGoals.getDouble(3)
            };
        } else {
            dataGoals = new double[] {0, 0, 0, 0};
        }
        cursorGoals.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ecoapp_fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Set nutrition goal settings
        int[] editViews = {
            R.id.editTextSettingsGoalsCal,
            R.id.editTextSettingsGoalsFat,
            R.id.editTextSettingsGoalsCarbs,
            R.id.editTextSettingsGoalsProtein
        };

        for (int i = 0; i < editViews.length; i++) {
            EditText editTextCalories = getView().findViewById(editViews[i]);
            editTextCalories.setText(Common.convertDataToText(dataGoals[i]));
            editTextCalories.addTextChangedListener(new textWatcher(i));
        }

        // Button
        saveButton = getView().findViewById(R.id.buttonSaveSettings);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savePossible) {
                    savePossible = false;
                    saveButton.setBackgroundResource(R.drawable.ecoapp_shape_box_round_middle);
                    saveButton.setTextColor(getContext().getColor(R.color.text_middle));
                    saveButton.setVisibility(View.INVISIBLE);

                    ((ActivityMain) requireContext()).SQLiteDatabase.setSettingsGoals(dataGoals[0], dataGoals[1], dataGoals[2], dataGoals[3]);

                }
            }
        });
    }

}