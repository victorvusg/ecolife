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

    // Table foods
    private static final String TABLE_PM = "preset_meals";
    private static final String COL_PM_INDEX = "meal_index";
    private static final String COL_PM_NAME = "name";
    private static final String COL_PM_CATEGORY = "category";
    private static final String COL_PM_CALORIES = "calories";
    private static final String COL_PM_FAT = "fat";
    private static final String COL_PM_FAT_SAT = "fat_sat";
    private static final String COL_PM_CARBS = "carbs";
    private static final String COL_PM_SUGAR = "sugar";
    private static final String COL_PM_PROTEIN = "protein";

    private static final String TABLE_PMC = "meal_categories";
    private static final String COL_PMC_NAME = "name";

    // Table meals per day
    private static final String TABLE_CM = "consumed_meals";
    private static final String COL_CM_DATE = "date";
    private static final String COL_CM_INDEX = "meal_index";  // Refers to uuid-index of a food from foods-table
    private static final String COL_CM_AMOUNT = "amount";

    // Table settings
    private static final String TABLE_S_GOAL = "settings_goals";
    private static final String COL_S_INDEX = "settings_index";
    private static final String COL_S_GOAL_CALORIES = "goal_calories";
    private static final String COL_S_GOAL_FAT = "goal_fat";
    private static final String COL_S_GOAL_CARBS = "goal_carbs";
    private static final String COL_S_GOAL_PROTEIN = "goal_protein";

    private static final String COL_S_LANG = "language";


    // Constructor ---------------------------------------------------------------------------------
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Class default overwrite methods -------------------------------------------------------------

    /** This method will be called upon creation of the database. This method will create all the
     * necessary tables inside the database and prepopulate some tables.
     *
     * @param sqLiteDatabase: SQLiteDatabase that is created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table food-data
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_PM + " ("
                 + COL_PM_INDEX + " TEXT PRIMARY KEY, "
                 + COL_PM_NAME + " TEXT, "
                 + COL_PM_CATEGORY + " TEXT, "
                 + COL_PM_CALORIES + " REAL, "
                 + COL_PM_FAT + " REAL, "
                 + COL_PM_FAT_SAT + " REAL, "
                 + COL_PM_CARBS + " REAL, "
                 + COL_PM_SUGAR + " REAL, "
                 + COL_PM_PROTEIN + " REAL);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PMC + " (" + COL_PMC_NAME + " TEXT PRIMARY KEY);");

        // Create table daily meals
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CM + " ("
                + COL_CM_DATE + " TEXT, "
                + COL_CM_INDEX + " TEXT, "
                + COL_CM_AMOUNT + " REAL, " +
                "PRIMARY KEY (" + COL_CM_INDEX + ", " + COL_CM_AMOUNT + "));"
        );

        // Create table settings
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_S_GOAL + " ("
                + COL_S_INDEX + " INTEGER PRIMARY KEY, "
                + COL_S_GOAL_CALORIES + " REAL, "
                + COL_S_GOAL_FAT + " REAL, "
                + COL_S_GOAL_CARBS + " REAL, "
                + COL_S_GOAL_PROTEIN + " REAL);");

        // Add preset data to tables ---------------------------------------------------------------

        // Preset meals
        // -> Format: index + name + 6 main values + 22 optional values
        // -> ('index', 'name', 'category', cal, fat, fatsat, carbs, sugar, protein, salt, fiber, cholesterin, creatine, ca, fe, k_potassium, mg, mn, na_sodium, phosphor, zn, vita, vitb1, b2, b3, b5, b6, b7, b11, b12, c, e, k, h)
        // -> Preset meals indices have always 1 digit more (7 digits in total) than user created meals to prevent overlaps
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PM + " VALUES('000000000', 'Apple (100 g)', 'Fruits and Vegetables', 52, 0.17, 0, 13.81, 10.39, 0.26, 0, 2.4, 0, 0, 6, 0.12, 107, 5, 0.035, 1, 11, 0.04, 0.003, 0.017, 0.026, 0.091, 0.061, 0.041, 0, 0, 0, 4.6, 0.18, 0.022, 0)");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PM + " VALUES('000000001', 'Banana (100 g)', 'Fruits and Vegetables', 95.0, 0.33, 0.0, 22.84, 12.23, 1.0, 0.0, 2.6, 0.0, 0.0, 5.0, 0.26, 358.0, 27.0, 0.0, 0.0, 22.0, 0.15, 0.003, 0.031, 0.073, 0.665, 0.334, 0.367, 0.0, 0.0, 0.0, 8.7, 0.0, 0.0, 0.0)");

        // Add meal categories
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Fruits and Vegetables');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Drinks');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Grains and Cereals');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Spices, Sauces, Oils');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Veggie Products');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Sweets and Spread');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Animal Products');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Convenience Foods');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Supplements');");
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PMC + " VALUES('Custom');");

        // Settings
        // -> (index, calories, carbs, fat, protein). First value is index. Must always be 0.
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_S_GOAL + " VALUES(0, 2500, 200, 100, 160)");

    }

    /** This method will be called upon upgrading the database from one version to a higher one.
     *
     * @param sqLiteDatabase: SQLiteDatabase; The SQLiteDatabase to upgrade.
     * @param oldVersion: Integer; The old version number of the database to upgrade from.
     * @param newVersion: Integer; The new version number of the database to upgrade to.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Delete old tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PMC);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_S_GOAL);

        // Create new database
        onCreate(sqLiteDatabase);
    }


    // Meals Query's -------------------------------------------------------------------------------

    public Cursor getPresetMealsSimpleAllCategories() {
        // -> Return index, name, calories from table "foods" in ascending order by name

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + COL_PM_INDEX + ", " + COL_PM_NAME + ", " + COL_PM_CALORIES + " FROM " + TABLE_PM + " ORDER BY " + COL_PM_NAME +  " ASC LIMIT 100;",
                    null
            );
        }

        return cursor;
    }

    public Cursor getPresetMealsSimpleFromCategory(String category) {
        // -> Return index, name, calories from specified category table "foods" in ascending order by name

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + COL_PM_INDEX + ", " + COL_PM_NAME + ", " + COL_PM_CALORIES + " FROM " + TABLE_PM + " WHERE " + COL_PM_CATEGORY + "='" + category + "'" + " ORDER BY " + COL_PM_NAME +  " ASC;",
                    null
            );
        }

        return cursor;
    }

    public Cursor getPresetMealDetails(String foodUUID) {
        // -> Returns all Details for a preset meal with given UUID

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PM + " WHERE " + COL_PM_INDEX + "='" + foodUUID + "';", null);
        }

        return cursor;
    }

    public Cursor getPresetMealCategories() {
        // -> Returns all Details for a preset meal with given UUID

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT " + COL_PMC_NAME + " FROM " + TABLE_PMC + " ORDER BY " + COL_PMC_NAME + " ASC;", null);
        }

        return cursor;
    }

    public Cursor getConsumedMeals(String date) {
        // -> Returns index, name, calories, amount for all consumed meals for a given date

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            // SELECT food.food_index, food.name, food.calories, dailymeals.amount FROM dailymeals LEFT JOIN food ON dailymeals.food_index=food.food_index WHERE dailymeals.date=date;
            // Returns -> | food.food_index | food.name | food.cal | dailymeals.amount |
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + TABLE_PM + "." + COL_PM_INDEX + ", " + TABLE_PM + "." + COL_PM_NAME + ", " + TABLE_PM + "." + COL_PM_CALORIES + ", " + TABLE_CM + "." + COL_CM_AMOUNT + " " +
                            "FROM " + TABLE_CM + " " +
                            "LEFT JOIN " + TABLE_PM + " ON " + TABLE_CM + "." + COL_CM_INDEX + "=" + TABLE_PM + "." + COL_PM_INDEX + " " +
                            "WHERE " + TABLE_CM + "." + COL_CM_DATE + "='" + date + "';",
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
                            "SUM (" + TABLE_PM + "." + COL_PM_CALORIES + " * " + TABLE_CM + "." + COL_CM_AMOUNT + "), " +
                            "SUM (" + TABLE_PM + "." + COL_PM_FAT + " * " + TABLE_CM + "." + COL_CM_AMOUNT + "), " +
                            "SUM (" + TABLE_PM + "." + COL_PM_FAT_SAT + " * " + TABLE_CM + "." + COL_CM_AMOUNT + "), " +
                            "SUM (" + TABLE_PM + "." + COL_PM_CARBS + " * " + TABLE_CM + "." + COL_CM_AMOUNT + "), " +
                            "SUM (" + TABLE_PM + "." + COL_PM_SUGAR + " * " + TABLE_CM + "." + COL_CM_AMOUNT + "), " +
                            "SUM (" + TABLE_PM + "." + COL_PM_PROTEIN + " * " + TABLE_CM + "." + COL_CM_AMOUNT + ") " +

                            "FROM " + TABLE_CM + " " +
                            "LEFT JOIN " + TABLE_PM + " ON " + TABLE_CM + "." + COL_CM_INDEX + "=" + TABLE_PM + "." + COL_PM_INDEX + " " +
                            "WHERE " + TABLE_CM + "." + COL_CM_DATE + "='" + date + "';",
                    null);
        }

        return cursor;
    }

    public void addOrReplacePresetMeal(String uuid, String name, String category, double[] data) {
        // -> Inserts new preset meal to database

        // Get database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Create content values to put into the database
        ContentValues cv = new ContentValues();

        cv.put(COL_PM_INDEX, uuid);
        cv.put(COL_PM_NAME, name);
        cv.put(COL_PM_CATEGORY, category);
        cv.put(COL_PM_CALORIES, data[0]);
        cv.put(COL_PM_FAT, data[1]);
        cv.put(COL_PM_FAT_SAT, data[2]);
        cv.put(COL_PM_CARBS, data[3]);
        cv.put(COL_PM_SUGAR, data[4]);
        cv.put(COL_PM_PROTEIN, data[5]);

        // Insert data into database
        long result = sqLiteDatabase.replaceOrThrow(TABLE_PM, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        }

    }

    public void addOrReplaceConsumedMeal(String date, String mealUUID, double amount) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_CM_INDEX, mealUUID);
        cv.put(COL_CM_DATE, date);
        cv.put(COL_CM_AMOUNT, amount);

        sqLiteDatabase.replaceOrThrow(TABLE_CM, null, cv);
    }

    public void removeConsumedMeal(String date, String mealUUID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.delete(TABLE_CM, COL_CM_INDEX + "= ? AND " + COL_CM_DATE + "= ?", new String[] {mealUUID, date});
        }
    }


    public Cursor getSettingsGoals() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;

        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + COL_S_GOAL_CALORIES + ", " + COL_S_GOAL_FAT + ", " + COL_S_GOAL_CARBS + ", " + COL_S_GOAL_PROTEIN +
                    " FROM " + TABLE_S_GOAL +
                    " WHERE " + COL_S_INDEX + "=0;",
                    null);
        }
        return cursor;
    }

    public void setSettingsGoals(double goalCalories, double goalFat, double goalCarbs, double goalProtein) {
        // Get database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Create content values to put into the database
        ContentValues cv = new ContentValues();

        cv.put(COL_S_INDEX, 0);
        cv.put(COL_S_GOAL_CALORIES, goalCalories);
        cv.put(COL_S_GOAL_FAT, goalFat);
        cv.put(COL_S_GOAL_CARBS, goalCarbs);
        cv.put(COL_S_GOAL_PROTEIN, goalProtein);

        long result = sqLiteDatabase.replaceOrThrow(TABLE_S_GOAL, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to save settings", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved settings", Toast.LENGTH_SHORT).show();
        }
    }

}
