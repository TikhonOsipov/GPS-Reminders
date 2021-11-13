package com.tixon.reminders.screen_reminders_list

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tixon.reminders.R
import com.tixon.reminders.items.ReminderItemView
import com.tixon.reminders.screen_reminders_list.adapter.ReminderRow
import com.tixon.reminders.screen_reminders_list.adapter.RemindersListAdapter
import com.tixon.reminders.screen_reminders_list.adapter.Row
import com.tixon.reminders.util.createBigTextNotification
import com.tixon.reminders.util.getPreferences
import com.tixon.reminders.util.Preference
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.tixon.reminders.1"
        private const val NOTIFICATION_CHANNEL_NAME = "GPS Reminders"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel description"
        private const val NOTIFICATION_ID_MAIN = 1
        private const val NOTIFICATION_ID_SECONDARY = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewmodel by lazy {
        ViewModelProvider(this,  viewModelFactory)
            .get(MainViewModel::class.java)
    }

    private val adapter = RemindersListAdapter<Row>()
    private val disposables = CompositeDisposable()

    private var lat by Preference(this)
    private var lon by Preference(this)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rvReminders)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewmodel.remindersLiveData.observe(this, { remindersList ->
            adapter.setData(remindersList.map { reminder ->
                ReminderRow(
                    id = "reminderRow",
                    hashCode = hashCode(),
                    data = ReminderItemView.Data(
                        title = reminder.title,
                        checked = reminder.isCompleted
                    )
                )
            })
        })

        viewmodel.load()

        createNotificationChannel()

        findViewById<FloatingActionButton>(R.id.fabReminders).setOnClickListener {
            /*showNotification(
                notificationId = NOTIFICATION_ID_MAIN,
                notification = createMainNotification()
            )*/

            /*startActivity(
                Intent(this, MapActivity::class.java)
            )*/

        }

        adapter.onItemClick = { itemText ->
            /*showNotification(
                notificationId = NOTIFICATION_ID_SECONDARY,
                notification = createSecondaryNotification(title = itemText)
            )*/
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { granted ->
                    if (granted) {

                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            5_000L,
                            0f,
                            LocalLocationListener()
                        )
                    }
                },
                onError = { t ->
                    Toast.makeText(
                        this,
                        "GPS permission error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
            .addTo(disposables)
    }

    inner class LocalLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("myLogs", "LOCATION changed: lat = ${location.latitude}, lon = ${location.longitude}")

            if (getPreferences().contains("lat").not() || getPreferences().contains("lon").not()) {
                return
            }

            val distanceArray = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                lat, lon,
                distanceArray
            )

            val distance = distanceArray[0]

            if (distance < 100) {
                showNotification(
                    notificationId = NOTIFICATION_ID_MAIN,
                    notification = createBigTextNotification(
                        title = "You are near to",
                        bigContentText = "Distance: $distance m",
                        priority = NotificationCompat.PRIORITY_MAX
                    )
                )
            }
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
}