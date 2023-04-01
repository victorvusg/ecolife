package com.ecolife.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ecolife.R;
import com.ecolife.data.DatabaseHelper;

import java.text.DecimalFormat;


public class ActivityMealsCreateEditPreset extends AppCompatActivity {

    /**
     * This activity lets the user create new meal-presets or edit already existing ones.
     */

    private String mealName;
    private int id;
    private double[] mealData = {0, 0, 0, 0, 0, 0, 0};
    private boolean savePossible = false;
    private String date;
    private String mode = "create";

    private Button saveButton;
    private Button cancelButton;
    private EditText editTextMealName;

    private DatabaseHelper databaseHelper;


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
            if (id == 0) {
                if (editable.toString().length() > 28) {
                    editTextMealName.setText(mealName);
                    Toast.makeText(getApplicationContext(), "Text limit reached!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mealName = editable.toString();
            } else {
                mealData[id - 1] = Double.parseDouble(editable.toString());
            }

            // Update background resource of save button
            saveButton.setBackgroundResource(R.drawable.shape_box_round_pop);
            saveButton.setTextColor(getColor(R.color.text_high));
            savePossible = true;
            cancelButton.setText(R.string.button_text_cancel);
        }
    }

    private String convertDataToText(double value) {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_createeditpreset);

        // Connect to database
        databaseHelper = new DatabaseHelper(ActivityMealsCreateEditPreset.this);

        Intent intent = getIntent();
        if (getIntent().hasExtra("date")) {
            date = intent.getStringExtra("date");
        }

        if (getIntent().hasExtra("mode")) {
            if (intent.getStringExtra("mode").equals("edit")) {
                mode = "edit";

                String uuid = intent.getStringExtra("uuid");
                Cursor cursorData = databaseHelper.getPresetMealDetailByID(uuid);
                if (cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    id = cursorData.getInt(0);
                    mealName = cursorData.getString(1);
                    mealData = new double[]{
                            cursorData.getDouble(2),
                            cursorData.getDouble(3),
                            cursorData.getDouble(4),
                            cursorData.getDouble(5),

                    };
                }
                cursorData.close();

            }
        }

        // -----------------------------------------------------------------------------------------
        // Set up edit-texts
        editTextMealName = findViewById(R.id.editTextMealName);
        if (mealName != null) {
            editTextMealName.setText(mealName);
        }
        editTextMealName.addTextChangedListener(new textWatcher(0));

        // TODO: Add new edit texts for new items
        EditText editTextCalories = findViewById(R.id.editTextCalories);
        editTextCalories.setHint(convertDataToText(mealData[0]));
        editTextCalories.addTextChangedListener(new textWatcher(1));

        EditText editTextProtein = findViewById(R.id.editTextProtein);
        editTextProtein.setHint(convertDataToText(mealData[1]));
        editTextProtein.addTextChangedListener(new textWatcher(2));

        EditText editTextFat = findViewById(R.id.editTextFat);
        editTextFat.setHint(convertDataToText(mealData[2]));
        editTextFat.addTextChangedListener(new textWatcher(3));

        EditText editTexFatSat = findViewById(R.id.editTextFatSat);
        editTexFatSat.setHint(convertDataToText(mealData[3]));
        editTexFatSat.addTextChangedListener(new textWatcher(4));

        EditText editTextCarbs = findViewById(R.id.editTextCarbs);
        editTextCarbs.setHint(convertDataToText(mealData[4]));
        editTextCarbs.addTextChangedListener(new textWatcher(5));

        EditText editTextSugar = findViewById(R.id.editTextWater);
        editTextSugar.setHint(convertDataToText(mealData[5]));
        editTextSugar.addTextChangedListener(new textWatcher(6));

        // -----------------------------------------------------------------------------------------
        // Set up toolbar
        Toolbar toolbarActivityCreateMeal = (Toolbar) findViewById(R.id.toolbarActivityCreateMeal);
        if (mode.equals("create")) {
            toolbarActivityCreateMeal.setTitle(getResources().getString(R.string.dn_button_add));
        } else if (mode.equals("edit")) {
            toolbarActivityCreateMeal.setTitle(getResources().getString(R.string.dn_button_edit));
        }

        setSupportActionBar(toolbarActivityCreateMeal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // -----------------------------------------------------------------------------------------
        // Set up buttons
        saveButton = findViewById(R.id.buttonSaveNewMeal);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savePossible) {
                    if (mealName == null) {
                        // If meal name was not set up yet remind user to add one
                        Toast.makeText(getApplicationContext(), "Please enter a name first!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    savePossible = false;

                    if (id == 0) {
                        id = 1;
                    }

                    // Save data to database
                    databaseHelper.addOrReplacePresetMeal(id, mealName, mealData);
                    databaseHelper.close();

                    // Change button color
                    saveButton.setBackgroundResource(R.drawable.shape_box_round_light);
                    saveButton.setTextColor(getColor(R.color.text_middle));
                    cancelButton.setText(R.string.button_text_back);
                }
            }
        });

        cancelButton = findViewById(R.id.buttonCancelNewMeal);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityMealsAddDailyEntry.class);
                if (date != null) {
                    intent.putExtra("date", date);
                }
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  // Start activity without animation
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}