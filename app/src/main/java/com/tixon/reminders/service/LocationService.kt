package com.tixon.reminders.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tixon.reminders.app.App
import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.storage.RemindersRepository
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
import javax.inject.Inject

class LocationService : DaggerService(), HasAndroidInjector {

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, this::class.java)
        }

        private const val NOTIFICATION_ID_MAIN = 1
        private const val NOTIFICATION_ID = 123
    }

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
                .flatMapIterable { it }
                .map { place ->
                    place.copy(
                        distance = calculateDistance(
                            from = location,
                            toPlace = place
                        )
                    )
                } //выставляем дистанцию
                .doOnNext { place ->
                    if (place.distance > 1200) {
                        val ignore = remindersRepository.markLocationAsNotified(place)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeBy(
                                onComplete = {},
                                onError = {
                                    Toast.makeText(this@LocationService, "Error doing 'markLocationAsNotified' for place with id ${place.id}: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                    }
                }
                .filter { it.workedInsideDistanceArea.not() }
                .filter { place ->
                    Toast.makeText(this@LocationService, "Distance in map: ${place.distance}", Toast.LENGTH_SHORT).show()
                    place.distance < 1200 //фильтруем по расстоянию (у каждого напоминания может быть своё расстояние)
                }
                .filter { it.reminderId != null } //пропускаем непривязанные локации
                .doOnNext { place ->
                    val ignore = remindersRepository.markLocationAsNotified(place)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeBy(
                            onComplete = {},
                            onError = {
                                Toast.makeText(this@LocationService, "Error doing 'markLocationAsNotified' for place with id ${place.id}: $it", Toast.LENGTH_SHORT).show()
                            }
                        )
                }
                .flatMap { place ->
                    Log.d("myLogs", "place: $place")
                    remindersRepository.getReminderById(
                        reminderId = place.reminderId ?: 0
                    ).map { it.copy(distance = place.distance) }
                } //получаем уведомление по локации
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = { reminder ->
                        showNotification(
                            createMainNotification(
                                title = reminder.title,
                                distance = reminder.distance
                            ),
                        )
                    },
                    onError = {
                        Toast.makeText(this@LocationService, "Error: $it", Toast.LENGTH_SHORT).show()
                        it.printStackTrace()
                    },
                )
                .addTo(disposables)
        }
    }

    private fun calculateDistance(from: Location, toPlace: PlaceLocation): Float {
        val distanceArray = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            toPlace.latitude, toPlace.longitude,
            distanceArray
        )
        return distanceArray[0]
    }

    private fun showNotification(
        notification: Notification,
        notificationId: Int = NOTIFICATION_ID_MAIN
    ) {
        NotificationManagerCompat.from(this).notify(
            notificationId,
            notification
        )
    }

    private fun createMainNotification(title: String, distance: Float): Notification {
        return createBigTextNotification(
            title = title,
            bigContentText = "Расстояние: $distance м",
            priority = NotificationCompat.PRIORITY_MAX
        )
    }
}
