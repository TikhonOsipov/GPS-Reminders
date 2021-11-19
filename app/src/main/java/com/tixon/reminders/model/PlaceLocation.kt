package com.tixon.reminders.model

import com.tixon.reminders.storage.entity.LocationDb

class PlaceLocation(
    val latitude: Double,
    val longitude: Double,
    val reminderId: Long? = null
)

fun List<PlaceLocation>.prepareForDb(): List<LocationDb> {
    return this.map {
        LocationDb(
            latitude = it.latitude,
            longitude = it.longitude
        )
    }
}
