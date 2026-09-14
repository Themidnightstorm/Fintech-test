package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_ID_MONTHLY_REVIEW = "vault_monthly_review_channel"
    private const val NOTIFICATION_ID_MONTHLY_REVIEW = 1001

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Monthly Financial Reviews"
            val descriptionText = "Notifications when your Monthly Executive Wealth Review is generated."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID_MONTHLY_REVIEW, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun sendMonthlyReviewNotification(context: Context, monthName: String = "This Month") {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_MONTHLY_REVIEW", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_REVIEW)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("Vault+ Monthly Review Ready")
            .setContentText("Your financial report card for $monthName is finalized. View net worth growth & milestones.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your $monthName Executive Wealth Report Card has been compiled. Check your savings vs goal, debt reduction, streak consistency, and custom recommendations.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(NOTIFICATION_ID_MONTHLY_REVIEW, builder.build())
            }
        } catch (_: SecurityException) {
            // Handled gracefully if permission not granted
        }
    }
}
