package com.tixon.reminders.screen_reminders_iist

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tixon.reminders.R
import com.tixon.reminders.items.ReminderItemView
import com.tixon.reminders.screen_reminders_iist.adapter.ReminderRow
import com.tixon.reminders.screen_reminders_iist.adapter.RemindersListAdapter
import com.tixon.reminders.screen_reminders_iist.adapter.Row

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.tixon.reminders.1"
        private const val NOTIFICATION_CHANNEL_NAME = "GPS Reminders"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel description"
        private const val NOTIFICATION_ID_MAIN = 1
        private const val NOTIFICATION_ID_SECONDARY = 2
    }

    private val adapter = RemindersListAdapter<Row>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rvReminders)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        adapter.setData(
            (0..100).map { num ->
                ReminderRow(
                    id = "reminderRow",
                    hashCode = hashCode(),
                    data = ReminderItemView.Data(
                        title = "Reminder $num",
                        checked = false
                    )
                )
            }
        )

        createNotificationChannel()

        findViewById<FloatingActionButton>(R.id.fabReminders).setOnClickListener {
            showNotification(
                notificationId = NOTIFICATION_ID_MAIN,
                notification = createMainNotification()
            )
        }

        adapter.onItemClick = { itemText ->
            showNotification(
                notificationId = NOTIFICATION_ID_SECONDARY,
                notification = createSecondaryNotification(title = itemText)
            )
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            val descriptionText = NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        notification: Notification,
        notificationId: Int
    ) {
        NotificationManagerCompat.from(this).notify(
            notificationId,
            notification
        )
    }

    private fun createMainNotification(): Notification {
        return createBigTextNotification(
            title = "This is title",
            bigContentText = "Much longer text that cannot fit one line...\n" +
                "Much longer text that cannot fit one line...",
            priority = NotificationCompat.PRIORITY_MAX
        )
    }

    private fun createSecondaryNotification(title: String): Notification {
        return createBigTextNotification(
            title = title,
            bigContentText = "This is text for big secondary notification",
            smallIcon = android.R.drawable.ic_delete
        )
    }

    private fun createBigTextNotification(
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
}