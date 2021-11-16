package com.tixon.reminders.screen_reminders_list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tixon.reminders.R
import com.tixon.reminders.util.toLatLng
import com.tixon.reminders.util.toPlaceLocation
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class AddReminderFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewmodel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(AddReminderViewModel::class.java)
    }

    private var map: MapView? = null
    private var buttonSave: Button? = null
    private var etReminderTitle: TextInputEditText? = null
    private var googleMap: GoogleMap? = null

    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_reminder, container, false)
        .apply {
            map = findViewById(R.id.mapReminderPoint)
            map?.getMapAsync(this@AddReminderFragment)
            map?.onCreate(savedInstanceState)

            etReminderTitle = findViewById(R.id.etReminderTitle)

            buttonSave = findViewById(R.id.buttonAddReminder)
            buttonSave?.setOnClickListener {
                etReminderTitle?.text?.toString()?.let { reminderTitle ->
                    viewmodel.save(reminderTitle)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onComplete = {
                                findNavController().popBackStack()
                            },
                            onError = {
                                it.printStackTrace()
                            }
                        ).addTo(disposables)
                }
            }
        }

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        map?.onPause()
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setOnMapClickListener { latLng ->
            viewmodel.addPlace(latLng.toPlaceLocation())
            Toast.makeText(
                requireContext(),
                "pending location size: ${viewmodel.getPendingLocations().size}",
                Toast.LENGTH_SHORT
            ).show()
            googleMap.addMarker(
                MarkerOptions().position(
                    latLng
                )
            )
        }

        googleMap.setOnMarkerClickListener { marker ->
            AlertDialog.Builder(requireContext())
                .setTitle("Хотите удалить точку?")
                .setPositiveButton("Удалить") { dialog, _ ->
                    viewmodel.removePlace(marker.position.toPlaceLocation())
                    Toast.makeText(
                        requireContext(),
                        "pending location size: ${viewmodel.getPendingLocations().size}",
                        Toast.LENGTH_SHORT
                    ).show()
                    marker.remove()
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            true
        }

        RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { granted ->
                    if (granted) {

                        /*locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            5_000L,
                            0f,
                            LocalLocationListener()
                        )*/

                        googleMap.isMyLocationEnabled = true
                        (requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { lastKnownLocation ->
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    lastKnownLocation.toLatLng(),
                                    15f
                                )
                            )
                        }
                    }
                },
                onError = { t ->
                    Toast.makeText(
                        context,
                        "GPS permission error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
            .addTo(disposables)
    }
}
