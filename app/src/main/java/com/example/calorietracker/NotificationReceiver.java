package com.example.calorietracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "calorie_tracker_channel";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "Broadcast received by NotificationReceiver!");

        createNotificationChannel(context);

        Intent intentToLaunch = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intentToLaunch, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Calorie Tracker")
                .setContentText("Don't forget to log your dinner!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(101, builder.build());
        Log.d("NotificationReceiver", "Broadcast received. Showing notification.");

    }

    private void createNotificationChannel(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Meal Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

}
