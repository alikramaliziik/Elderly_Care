package old.people.elderly_care

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ActivityNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val activityTitle = intent.getStringExtra("activity_title") ?: "Activity Reminder"
        val elderlyName = intent.getStringExtra("elderly_name") ?: "Elderly"
        val activityTime = intent.getStringExtra("activity_time") ?: ""
        val activityId = intent.getIntExtra("activity_id", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Activity Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for elderly care activities"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open the app when notification is tapped
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            activityId,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification - USE R.drawable.ic_notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)  // FIXED
            .setContentTitle("Activity Reminder: $activityTitle")
            .setContentText("Time: $activityTime - For: $elderlyName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)  
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(activityId, notification)
    }

    companion object {
        private const val CHANNEL_ID = "elderly_care_activity_reminders"
    }
}
