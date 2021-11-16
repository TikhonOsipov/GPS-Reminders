package com.tixon.reminders.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.tixon.reminders.app.App
import com.tixon.reminders.util.getPreferences
import com.tixon.reminders.util.Preference
import com.tixon.reminders.util.createBigTextNotification
import io.reactivex.disposables.CompositeDisposable

class LocationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 123
    }

    private var lat by Preference(this)
    private var lon by Preference(this)

    private val disposables = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val repo = App.get(this).appComponent.remindersRepository()
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            startForeground(
                NOTIFICATION_ID,
                createBigTextNotification(
                    "Работаю в Foreground",
                    "Это ForegroundService для location",
                )
            )
            Toast.makeText(
                this,
                "LocationService started, granted",
                Toast.LENGTH_LONG
            ).show()
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5_000L,
                0f,
                LocalLocationListener()
            )
        } else {
            Toast.makeText(
                this,
                "LocationService: GPS permission denied",
                Toast.LENGTH_LONG
            ).show()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    inner class LocalLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {

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

            Toast.makeText(this@LocationService, "[Service] d: $distance", Toast.LENGTH_SHORT).show()
        }
    }
}