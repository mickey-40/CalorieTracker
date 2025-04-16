package com.example.calorietracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private ArrayList<Meal> mealList;
    private OnDeleteClickListener deleteClickListener;

    public MealAdapter(ArrayList<Meal> mealList, OnDeleteClickListener deleteClickListener) {
        this.mealList = mealList;
        this.deleteClickListener = deleteClickListener;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.tvMealName.setText(meal.getName());
        holder.tvCalories.setText("Calories: " + meal.getCalories());
        holder.tvMealType.setText("Meal Type: " + meal.getMealType());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.onDeleteClick(meal);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName, tvCalories, tvMealType;
        Button btnDelete;

        public MealViewHolder(View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Meal meal);
    }
}
