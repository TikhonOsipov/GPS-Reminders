package com.tixon.reminders.model

import com.tixon.reminders.storage.entity.LocationDb

/**
 * Предыдущая entity в иерархии: [LocationDb]
 */
data class PlaceLocation(
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val reminderId: Long? = null,
    val distance: Float = 0f,
    val workedInsideDistanceArea: Boolean = false,
)

fun List<PlaceLocation>.prepareForDb(): List<LocationDb> {
    return this.map(::mapPlaceLocation)
}

fun PlaceLocation.prepareForDb(): LocationDb = mapPlaceLocation(this)

private fun mapPlaceLocation(placeLocation: PlaceLocation): LocationDb = with (placeLocation) {
    LocationDb(
        locationId = id,
        latitude = latitude,
        longitude = longitude,
        workedInsideDistanceArea = workedInsideDistanceArea,
    )
}
