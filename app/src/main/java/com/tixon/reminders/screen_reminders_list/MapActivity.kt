package com.tixon.reminders.screen_reminders_list

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tixon.reminders.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tixon.reminders.service.LocationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: MapView? = null
    private var googleMap: GoogleMap? = null
    private var toolbar: Toolbar? = null
    private val disposables = CompositeDisposable()

    private var lat by preference(this)
    private var lon by preference(this)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        map = findViewById(R.id.mapView)
        map?.getMapAsync(this)

        map?.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbarMap)
        setSupportActionBar(toolbar)

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

                        googleMap?.isMyLocationEnabled = true

                        startService(
                            Intent(this, LocationService::class.java)
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

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        map?.onPause()
        super.onPause()
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        googleMap?.addMarker(
            MarkerOptions().position(
                LatLng(lat, lon)
            )
        )

        val svdLat = lat
        val svdLon = lon

        if (svdLat != 0.0 && svdLon != 0.0) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(svdLat, svdLon),
                    15f
                )
            )
        }

        googleMap?.setOnMapClickListener { latLng ->
            lat = latLng.latitude
            lon = latLng.longitude

            Log.d("myLogs", "saved marker position: lat = $svdLat, lon = $svdLon")

            googleMap?.clear()

            googleMap?.addMarker(
                MarkerOptions().position(latLng)
            )
        }
    }

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

            toolbar?.title = "d: $distance"
            //Toast.makeText(this@MapActivity, "d: $distance", Toast.LENGTH_SHORT).show()
        }
    }
}
