package com.ecolife.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolife.ActivityMain;
import com.ecolife.R;
import com.ecolife.data.DatabaseHelper;
import com.ecolife.recyclerview.AdapterMealPresets;
import com.ecolife.recyclerview.ItemMealPreset;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ActivityMealsAddDailyEntry extends AppCompatActivity implements AdapterMealPresets.mealPresetItemInterface {

    /**
     * This activity displays all meal-presets and lets the user add them to the database
     * for the current day.
     */

    private String date;
    private boolean savePossible = false;

    private Button saveButton;
    private Button cancelButton;
    TextView noEntries;
    RecyclerView recyclerViewMeals;

    private ArrayList<ItemMealPreset> mealsPresetList;
    private AdapterMealPresets adapterPresets;

    private DatabaseHelper databaseHelper;

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

    private ArrayList<ItemMealPreset> loadPresetMealsFromDatabase() {
        Cursor cursor = databaseHelper.getAllPresetMeals();

        ArrayList<ItemMealPreset> loadedPresets = new ArrayList<ItemMealPreset>();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Create new ItemMealPreset and add it to meals-list
                loadedPresets.add(cursor.getPosition(), new ItemMealPreset(
                        cursor.getString(1),  // Title
                        cursor.getString(0),  // UUID
                        (int) cursor.getDouble(2),  // Calories
                        0  // Amount
                ));
            }
        }
        cursor.close();

        return loadedPresets;
    }


    // Overwrite class default methods -------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_adddailyentry);

        // Set loading-layout ----------------------------------------------------------------------
        FrameLayout loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.VISIBLE);

        // Get data from intent --------------------------------------------------------------------
        Intent intent = getIntent();
        if (getIntent().hasExtra("date")) {
            date = intent.getStringExtra("date");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = formatter.format(new Date());
        }

        // Set up toolbar --------------------------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbarActivityAddMeal);
        toolbar.setTitle(getResources().getString(R.string.dn_button_add));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up categories spinner ---------------------------------------------------------------

        databaseHelper = new DatabaseHelper(ActivityMealsAddDailyEntry.this);

        // Load preset meals from database
        mealsPresetList = loadPresetMealsFromDatabase();

        // Set adapters and recycler views
        adapterPresets = new AdapterMealPresets(mealsPresetList, this, getApplicationContext());
        recyclerViewMeals = findViewById(R.id.recyclerViewMealsPreset);
        recyclerViewMeals.setAdapter(adapterPresets);
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        // Update loading-layout -------------------------------------------------------------------
        noEntries = findViewById(R.id.textViewNoEntries);
        if (mealsPresetList.isEmpty()) {
            noEntries.setVisibility(View.VISIBLE);
        }

        loadingLayout.setVisibility(View.INVISIBLE);

        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(date);

        // Set up buttons --------------------------------------------------------------------------
        Button buttonAddMeal = findViewById(R.id.buttonAddMeal);
        buttonAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start new activity Activity_CreateMeal
                Intent intent = new Intent(view.getContext(), ActivityMealsCreateEditPreset.class);
                if (date != null) {
                    intent.putExtra("date", date);
                }
                startActivity(intent);
            }
        });

        saveButton = findViewById(R.id.buttonAddMealSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savePossible) {
                    savePossible = false;

                    for (ItemMealPreset currentMeal : mealsPresetList) {
                        if (currentMeal.getAmount() > 0) {
                            databaseHelper.addOrReplaceConsumedMeal(date, currentMeal.getMealUUID(), currentMeal.getAmount());
                        }
                    }

                    saveButton.setBackgroundResource(R.drawable.shape_box_round_light);
                    cancelButton.setText("Back");
                    Snackbar.make(view, "Saved", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton = findViewById(R.id.buttonAddMealCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start new activity ActivityMain (= go back to main screen)
                Intent intent = new Intent(view.getContext(), ActivityMain.class);
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


    // Methods from imported interface -------------------------------------------------------------

    @Override
    public void updateItemAmount(int itemPosition, String mealUUID, double newAmount) {
        if (!savePossible) {
            savePossible = true;
            saveButton.setBackgroundResource(R.drawable.shape_box_round_pop);
            saveButton.setTextColor(getColor(R.color.text_high));
        }
        adapterPresets.notifyItemChanged(itemPosition);  // Update view
    }

    @Override
    public void onItemClick(String mealUUID) {
        // Start new activity Activity_CreateMeal
        Intent intent = new Intent(getApplicationContext(), ActivityMealsCreateEditPreset.class);
        if (date != null) {
            intent.putExtra("date", date);
        }
        intent.putExtra("mode", "edit");
        intent.putExtra("uuid", mealUUID);
        startActivity(intent);
    }

    @Override
    public void onAmountClick(int itemPosition) {
        // Show dialog to edit amount
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(view);

        EditText editTextPlanName = view.findViewById(R.id.dialogEditText);
        if (mealsPresetList.get(itemPosition).getAmount() != 0) {
            editTextPlanName.setText(convertDataToText(mealsPresetList.get(itemPosition).getAmount()));
        }
        editTextPlanName.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setTitle("Change amount");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String textInput = editTextPlanName.getText().toString();

                if (textInput.length() <= 0) {
                    return;
                }

                mealsPresetList.get(itemPosition).setAmount(Double.parseDouble(textInput));
                adapterPresets.notifyItemChanged(itemPosition);

                if (!savePossible) {
                    savePossible = true;
                    saveButton.setBackgroundResource(R.drawable.shape_box_round_pop);
                    saveButton.setTextColor(getColor(R.color.text_high));
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}