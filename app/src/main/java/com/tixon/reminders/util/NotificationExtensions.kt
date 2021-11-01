package com.tixon.reminders.util

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_CHANNEL_ID = "com.tixon.reminders.1"

fun Context.createBigTextNotification(
    title: String,
    bigContentText: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    @DrawableRes smallIcon: Int = android.R.drawable.ic_popup_reminder
): Notification {
    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setSmallIcon(smallIcon)
        .setContentText(bigContentText)
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText(bigContentText))
        .setPriority(priority)
        .build()
}