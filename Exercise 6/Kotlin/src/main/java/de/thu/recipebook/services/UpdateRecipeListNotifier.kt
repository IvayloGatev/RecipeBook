package de.thu.recipebook.services

import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import de.thu.recipebook.R
import android.content.Intent
import de.thu.recipebook.activities.RecipeListActivity
import android.app.PendingIntent
import android.content.Context
import android.os.Build

class UpdateRecipeListNotifier(context: Context) {
    var notificationBuilder: NotificationCompat.Builder
    var notificationManager: NotificationManager

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "update_recipe_list_channel"
        private const val CHANNEL_DESCRIPTION = "Update recipe list state"
    }

    init {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_DESCRIPTION,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_new_foreground)
            .setContentTitle("Recipe Book")
            .setAutoCancel(false)

        val resultIntent = Intent(context, RecipeListActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val resultPendingIntent =
            PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(resultPendingIntent)
    }

    fun showOrUpdateNotification(value: String?) {
        notificationBuilder.setContentText(value)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}