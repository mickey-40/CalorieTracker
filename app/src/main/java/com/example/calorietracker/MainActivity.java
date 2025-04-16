package com.example.calorietracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etMealName, etCalories;
    Spinner spinnerMealType;
    Button btnAddMeal;
    TextView tvTotalCalories;
    RecyclerView rvMealList;

    DBHelper dbHelper;
    MealAdapter mealAdapter;
    ArrayList<Meal> mealList;
    String todayDate;

    Button btnPickDate;
    TextView tvSelectedDate;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent testIntent = new Intent(this, NotificationReceiver.class);
        sendBroadcast(testIntent);


        // Initialize Views
        etMealName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        spinnerMealType = findViewById(R.id.spinnerMealType);
        btnAddMeal = findViewById(R.id.btnAddMeal);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        rvMealList = findViewById(R.id.rvMealList);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        calendar = Calendar.getInstance();

        // Set up Spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(adapter);

        // default to today
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        tvSelectedDate.setText(todayDate);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            calendar.set(Calendar.YEAR, selectedYear);
                            calendar.set(Calendar.MONTH, selectedMonth);
                            calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                            todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                            tvSelectedDate.setText(todayDate);
                            loadMeals();
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Initialize DB
        dbHelper = new DBHelper(this);

        // Load meals and display
        loadMeals();

        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMeal();
            }
        });


        scheduleDailyNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }


    }

    private void addMeal() {
        String name = etMealName.getText().toString().trim();
        String caloriesText = etCalories.getText().toString().trim();
        String mealType = spinnerMealType.getSelectedItem().toString();

        if (name.isEmpty() || caloriesText.isEmpty()) {
            Toast.makeText(this, "Please enter name and calories", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(caloriesText);

        dbHelper.addMeal(name, calories, mealType, todayDate);
        etMealName.setText("");
        etCalories.setText("");

        loadMeals(); // refresh list + calories
    }

    private void loadMeals() {
        mealList = dbHelper.getMealsByDate(todayDate);
        int totalCalories = dbHelper.getTotalCaloriesToday(todayDate);
        tvTotalCalories.setText("Total Calories Today: " + totalCalories);

        // Set up RecyclerView
        mealAdapter = new MealAdapter(mealList, new MealAdapter.OnDeleteClickListener(){
            @Override
            public void onDeleteClick(Meal meal){
                dbHelper.deleteMeal(meal.getId());
                loadMeals();
            }
        });
        rvMealList.setLayoutManager(new LinearLayoutManager(this));
        rvMealList.setAdapter(mealAdapter);
    }

    private void scheduleDailyNotification() {
        Log.d("DEBUG", "Alarm scheduled for: " + calendar.getTime());

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm time (e.g. 7:00 PM)
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 19);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//      Testing
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10); // trigger in 10 seconds for demo purposes


        // If the time is already past today, schedule for tomorrow
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
        Log.d("MainActivity", "Notification scheduled for: " + calendar.getTime());
    }

}
