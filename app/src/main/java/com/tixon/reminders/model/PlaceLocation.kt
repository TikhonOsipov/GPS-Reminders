package com.tixon.reminders.model

import com.tixon.reminders.storage.entity.LocationDb

data class PlaceLocation(
    val latitude: Double,
    val longitude: Double,
    val reminderId: Long? = null,
    val distance: Float = 0f,
)

fun List<PlaceLocation>.prepareForDb(): List<LocationDb> {
    return this.map {
        LocationDb(
            latitude = it.latitude,
            longitude = it.longitude
        )
    }
}
