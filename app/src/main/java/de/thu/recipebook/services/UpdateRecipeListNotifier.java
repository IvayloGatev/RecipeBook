package de.thu.recipebook.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import de.thu.recipebook.R;
import de.thu.recipebook.activities.RecipeListActivity;

public class UpdateRecipeListNotifier {
    private static final int NOTIFICATION_ID = 123;
    private static String CHANNEL_ID = "update_recipe_list_channel";
    private static String CHANNEL_DESCRIPTION = "Update recipe list state";
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public UpdateRecipeListNotifier(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_DESCRIPTION,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_new_foreground)
                .setContentTitle("Recipe List")
                .setAutoCancel(false);
        Intent resultIntent = new Intent(context, RecipeListActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(resultPendingIntent);
    }

    public void showOrUpdateNotification(String value) {
        notificationBuilder.setContentText(value);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
