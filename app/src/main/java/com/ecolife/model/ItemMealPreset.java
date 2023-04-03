package com.ecolife.model;

/**
 * Item for Adapter_MealPreset.
 */

public class ItemMealPreset {
    private String _mealTitle;
    private String _uuid;
    private int _calories;
    private double _amount;

    public ItemMealPreset(String mealTitle, String uuid, int calories, double amount){
        this._mealTitle = mealTitle;
        this._uuid = uuid;
        this._calories = calories;
        this._amount = amount;
    }

    public String getMealTitle() {
        return this._mealTitle;
    }

    public String getMealUUID() {
        return this._uuid;
    }

    public int getCalories() {
        return this._calories;
    }

    public double getAmount() {
        return this._amount;
    }

    public void setAmount(double newAmount) {
        this._amount = newAmount;
    }

}
