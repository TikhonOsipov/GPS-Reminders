package com.tixon.reminders.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.tixon.reminders.app.App
import com.tixon.reminders.storage.RemindersRepository
import com.tixon.reminders.util.Preference
import com.tixon.reminders.util.createBigTextNotification
import dagger.android.AndroidInjector
import dagger.android.DaggerService
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import javax.inject.Inject

class LocationService : DaggerService(), HasAndroidInjector {

    companion object {
        private const val NOTIFICATION_ID = 123
    }

    private var lat by Preference(this)
    private var lon by Preference(this)

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = serviceInjector

    @Inject
    lateinit var remindersRepository: RemindersRepository

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
                "LocationService started, granted. Repo: ${repo.hashCode()}",
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

            remindersRepository.getLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = { locations ->
                        val remindersToNotify = locations.filter {
                            val distanceArray = FloatArray(1)
                            Location.distanceBetween(
                                location.latitude, location.longitude,
                                lat, lon,
                                distanceArray
                            )
                            distanceArray[0] < 1200
                        }
                            .map { it.reminderId }
                        if (remindersToNotify.isNotEmpty()) {
                            val sb = StringBuilder()
                            remindersToNotify.forEach { sb.append(it).append("; ") }
                            Toast.makeText(
                                this@LocationService,
                                "Reminders: ${sb.toString().trim()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onError = { it.printStackTrace() },
                )
                .addTo(disposables)
        }
    }
}