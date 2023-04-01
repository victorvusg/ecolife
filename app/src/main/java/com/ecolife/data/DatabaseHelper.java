package com.ecolife.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * This class contains all methods to access the database.
     */

    private Context context;
    private static final String DATABASE_NAME = "ecolife_database";
    private static final int DATABASE_VERSION = 43;
    private static final String DATABASE_COLUMN_INDEX = "id";

    /**
     * Table "Preset Meals"
     */
    private static final String PRESET_MEALS_TABLE_NAME = "preset_meals";

    private static final String PRESET_MEALS_COLUMN_MEAL_NAME = "name";

    private static final String PRESET_MEALS_COLUMN_CALORIES = "calories";
    private static final String PRESET_MEALS_COLUMN_PROTEIN = "protein";
    private static final String PRESET_MEALS_COLUMN_FAT = "total_fat";
    private static final String PRESET_MEALS_COLUMN_SAT_FAT = "sat_fat";
    private static final String PRESET_MEALS_COLUMN_CARBOHYDRATES = "carbohydrates";
    private static final String PRESET_MEALS_COLUMN_WATER = "water";

    /**
     * Table: "Daily meals"
     */
    private static final String CONSUMED_MEAL_TABLE_NAME = "consumed_meals";
    private static final String CONSUMED_MEAL_COLUMN_DATE = "date";
    private static final String CONSUMED_MEAL_COLUMN_AMOUNT = "amount";
    private static final String CONSUMED_MEAL_COLUMN_PRESET_ID = "preset_id";

    /**
     * Table: "Settings"
     */
    private static final String SETTINGS_TABLE_NAME = "settings_goals";
    private static final String SETTINGS_COLUMN_CALORIES_GOAL = "calories_goal";
    private static final String SETTINGS_COLUMN_PROTEIN_GOAL = "protein_goal";
    private static final String SETTINGS_COLUMN_FAT_GOAL = "fat_goal";
    private static final String SETTINGS_COLUMN_SAT_FAT_GOAL = "sat_fat_goal";
    private static final String SETTINGS_COLUMN_CARBOHYDRATES_GOAL = "carbohydrates_goal";
    private static final String SETTINGS_COLUMN_WATER_GOAL = "water_goal";
    // Constructor ---------------------------------------------------------------------------------
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /** This method will be called upon creation of the database. This method will create all the
     * necessary tables inside the database and prepopulate some tables.
     *
     * @param sqLiteDatabase: SQLiteDatabase that is created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /**
         * Create table meal preset
         */

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + PRESET_MEALS_TABLE_NAME + " ("
                 + DATABASE_COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                 + PRESET_MEALS_COLUMN_MEAL_NAME + " TEXT, "
                 + PRESET_MEALS_COLUMN_CALORIES + " REAL, "
                 + PRESET_MEALS_COLUMN_PROTEIN + " REAL, "
                 + PRESET_MEALS_COLUMN_FAT + " REAL, "
                + PRESET_MEALS_COLUMN_SAT_FAT + " REAL, "
                + PRESET_MEALS_COLUMN_CARBOHYDRATES + " REAL, "
                + PRESET_MEALS_COLUMN_WATER + " REAL);"
        );

        /**
         * Create table consumed meal
         */
        sqLiteDatabase.execSQL("CREATE TABLE " + CONSUMED_MEAL_TABLE_NAME + " ("
                + DATABASE_COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + CONSUMED_MEAL_COLUMN_DATE + " TEXT, "
                + CONSUMED_MEAL_COLUMN_AMOUNT + " REAL);");

        /**
         * Create table settings
         */
        sqLiteDatabase.execSQL("CREATE TABLE " + SETTINGS_TABLE_NAME + " ("
                + DATABASE_COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + SETTINGS_COLUMN_CALORIES_GOAL + " REAL, "
                + SETTINGS_COLUMN_PROTEIN_GOAL + " REAL, "
                + SETTINGS_COLUMN_FAT_GOAL + " REAL, "
                + SETTINGS_COLUMN_SAT_FAT_GOAL + " REAL, "
                + SETTINGS_COLUMN_CARBOHYDRATES_GOAL + " REAL, "
                + SETTINGS_COLUMN_WATER_GOAL + " REAL);");

        /**
         * Seed some preset data to tables
         */
        // Preset meals
        // -> Format: index + name + 6 main values + 22 optional values
        // -> ('name', calories, protein, fat, sat_fat, carbs, water)
        // -> Preset meals indices have always 1 digit more (7 digits in total) than user created meals to prevent overlaps
        sqLiteDatabase.execSQL("INSERT INTO " + PRESET_MEALS_TABLE_NAME +
                " VALUES(1, 'Apple (100 g)'," +
                "100, 0.17, 0, 13.81, 10.39, 100)");
        sqLiteDatabase.execSQL("INSERT INTO " + PRESET_MEALS_TABLE_NAME +
                " VALUES(2, 'Banana (100 g)'," +
                "95.0, 0.33, 0.0, 22.84, 12.23, 100)");

        // Settings
        // -> (index, calories, carbs, fat, protein). First value is index. Must always be 0.
        sqLiteDatabase.execSQL("INSERT INTO " + SETTINGS_TABLE_NAME + " VALUES(1, 2500, 100, 50, 20, 100, 160)");
    }

    /**
     * This method will be called upon upgrading the database from one version to a higher one.
     * @param sqLiteDatabase: SQLiteDatabase; The SQLiteDatabase to upgrade.
     * @param oldVersion: Integer; The old version number of the database to upgrade from.
     * @param newVersion: Integer; The new version number of the database to upgrade to.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Delete old tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRESET_MEALS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CONSUMED_MEAL_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);

        // Create new database
        onCreate(sqLiteDatabase);
    }


    /**
     * Meals Query's -------------------------------------------------------------------------------
     */


    /**
     * Get all preset meals
     * @return
     */
    public Cursor getAllPresetMeals() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT *  FROM " + PRESET_MEALS_TABLE_NAME + " ORDER BY " + DATABASE_COLUMN_INDEX +  " ASC LIMIT 100;",
                    null
            );
        }

        return cursor;
    }

    /**
     *
     * @param id
     * @return
     */
    public Cursor getPresetMealDetailByID(String id) {
        // -> Returns all Details for a preset meal with given UUID

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                    + PRESET_MEALS_TABLE_NAME + " WHERE " + DATABASE_COLUMN_INDEX + "='" + id + "';", null);
        }

        return cursor;
    }

    /**
     *
     * @param date
     * @return
     */
    public Cursor getConsumedMealsByDate(String date) {
        // -> Returns index, name, calories, amount for all consumed meals for a given date

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT * " +
                            "FROM " + CONSUMED_MEAL_TABLE_NAME +
                            " LEFT JOIN " + PRESET_MEALS_TABLE_NAME +
                            " ON " + CONSUMED_MEAL_TABLE_NAME + "."
                            + DATABASE_COLUMN_INDEX + "="
                            + PRESET_MEALS_TABLE_NAME + "." + DATABASE_COLUMN_INDEX +
                            " WHERE " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_DATE + "='" + date + "';",
                    null);
        }

        return cursor;
    }

    public Cursor getConsumedMealsSums(String date) {
        // -> Returns sum of all details of all consumed meals for a given date

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_CALORIES + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + "), " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_PROTEIN + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + "), " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_SAT_FAT + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + "), " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_SAT_FAT + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + "), " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_CARBOHYDRATES + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + "), " +
                            "SUM (" + PRESET_MEALS_TABLE_NAME + "." + PRESET_MEALS_COLUMN_WATER + " * " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_AMOUNT + ") " +

                            "FROM " + CONSUMED_MEAL_TABLE_NAME + " " +
                            "LEFT JOIN " + PRESET_MEALS_TABLE_NAME + " ON " + CONSUMED_MEAL_TABLE_NAME + "." + DATABASE_COLUMN_INDEX + "=" + PRESET_MEALS_TABLE_NAME + "." + DATABASE_COLUMN_INDEX + " " +
                            "WHERE " + CONSUMED_MEAL_TABLE_NAME + "." + CONSUMED_MEAL_COLUMN_DATE + "='" + date + "';",
                    null);
        }

        return cursor;
    }

    public void addOrReplacePresetMeal(String id, String name, double[] nutritionData) {
        // Get database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Create content values to put into the database
        ContentValues contentValues = new ContentValues();

        String[] nutritionNames = {
                PRESET_MEALS_COLUMN_CALORIES,
                PRESET_MEALS_COLUMN_PROTEIN,
                PRESET_MEALS_COLUMN_FAT,
                PRESET_MEALS_COLUMN_SAT_FAT,
                PRESET_MEALS_COLUMN_CARBOHYDRATES,
                PRESET_MEALS_COLUMN_WATER
        };

        for (int i = 0; i < nutritionNames.length; i++) {
            contentValues.put(nutritionNames[i], nutritionData[i]);
        }

        contentValues.put(DATABASE_COLUMN_INDEX, id);
        contentValues.put(PRESET_MEALS_COLUMN_MEAL_NAME, name);

        // Insert data into database
        long result = sqLiteDatabase.replaceOrThrow(PRESET_MEALS_TABLE_NAME, null, contentValues);

        if (result == -1) {
            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     *
     * @param date
     * @param id
     * @param amount
     */
    public void addOrReplaceConsumedMeal(String date, String id, double amount) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DATABASE_COLUMN_INDEX, id);
        contentValues.put(CONSUMED_MEAL_COLUMN_DATE, date);
        contentValues.put(CONSUMED_MEAL_COLUMN_AMOUNT, amount);

        sqLiteDatabase.replaceOrThrow(CONSUMED_MEAL_TABLE_NAME, null, contentValues);
    }

    /**
     *
     * @param id
     */
    public void removeConsumedMealById(String id) {
        // Remove entry from DailyMealsTable with date and UUID
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.delete(CONSUMED_MEAL_TABLE_NAME, DATABASE_COLUMN_INDEX + "= ?", new String[] {id});
        }
    }

    /**
     * Get settings
     * @return
     */
    public Cursor getSettingsGoals() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT *" +
                    " FROM " + SETTINGS_TABLE_NAME +
                    " WHERE " + DATABASE_COLUMN_INDEX + "=0;",
                    null);
        }

        return cursor;
    }

    /**
     *
     * @param goals include
     * SETTINGS_COLUMN_CALORIES_GOAL,
     * SETTINGS_COLUMN_PROTEIN_GOAL,
     * SETTINGS_COLUMN_FAT_GOAL,
     * SETTINGS_COLUMN_SAT_FAT_GOAL,
     * SETTINGS_COLUMN_CARBOHYDRATES_GOAL,
     * SETTINGS_COLUMN_WATER_GOAL,
     */
    public void setSettingsGoals(double[] goals) {
        // Get database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Create content values to put into the database
        ContentValues contentValues = new ContentValues();

        String[] goalNames = {
            SETTINGS_COLUMN_CALORIES_GOAL,
            SETTINGS_COLUMN_PROTEIN_GOAL,
            SETTINGS_COLUMN_FAT_GOAL,
            SETTINGS_COLUMN_SAT_FAT_GOAL,
            SETTINGS_COLUMN_CARBOHYDRATES_GOAL,
            SETTINGS_COLUMN_WATER_GOAL,
        };

        for (int i = 0; i < goalNames.length; i++) {
            contentValues.put(goalNames[i], goals[i]);
        }

        long result = sqLiteDatabase.replaceOrThrow(SETTINGS_TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed to save settings", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved settings", Toast.LENGTH_SHORT).show();
        }
    }
}
