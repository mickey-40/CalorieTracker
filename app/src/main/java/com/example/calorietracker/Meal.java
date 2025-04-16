package com.example.calorietracker;

public class Meal {
    private int id;
    private String name;
    private int calories;
    private String mealType;
    private String date;

    public Meal(int id, String name, int calories, String mealType, String date) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.mealType = mealType;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCalories() { return calories; }
    public String getMealType() { return mealType; }
    public String getDate() { return date; }
}
