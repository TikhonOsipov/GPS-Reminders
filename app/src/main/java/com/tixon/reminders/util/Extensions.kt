package com.tixon.reminders.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.android.gms.maps.model.LatLng

private const val PREF_NAME = "RemindersPreferences"

fun Activity.getPreferences(): SharedPreferences {
    return this.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
}

fun Context.getPreferences(): SharedPreferences {
    return this.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
}

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}
