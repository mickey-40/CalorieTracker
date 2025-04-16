package com.example.calorietracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CalorieTracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEALS = "meals";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_MEALTYPE = "meal_type";
    public static final String COLUMN_DATE = "date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEALS_TABLE = "CREATE TABLE " + TABLE_MEALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_CALORIES + " INTEGER,"
                + COLUMN_MEALTYPE + " TEXT,"
                + COLUMN_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_MEALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }

    public void addMeal(String name, int calories, String mealType, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_MEALTYPE, mealType);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_MEALS, null, values);
        db.close();
    }

    public ArrayList<Meal> getAllMeals() {
        ArrayList<Meal> mealList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEALS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Meal meal = new Meal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEALTYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                mealList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mealList;
    }

    public int getTotalCaloriesToday(String todayDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_MEALS + " WHERE " + COLUMN_DATE + "=?",
                new String[]{todayDate}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public void deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEALS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<Meal> getMealsByDate(String date) {
        ArrayList<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_MEALS + " WHERE " + COLUMN_DATE + "=?",
                new String[]{date}
        );

        if (cursor.moveToFirst()) {
            do {
                Meal meal = new Meal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEALTYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                mealList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mealList;
    }

}
