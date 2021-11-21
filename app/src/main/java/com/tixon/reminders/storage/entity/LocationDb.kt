package com.tixon.reminders.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица: Locations
 *
 * Местоположение, при нахождении рядом с которым возникает уведомление о напоминании [reminderIdRefersTo].
 *
 * @property locationId уникальный идентификатор местоположения
 * @property reminderIdRefersTo идентификатор напоминания, к которому относится местоположение
 * @property latitude ширина
 * @property longitude долгота
 *
 * Следующая entity в иерархии: [com.tixon.reminders.model.PlaceLocation]
 */
@Entity(
    tableName = "Locations",
)
data class LocationDb(
    @PrimaryKey(autoGenerate = true)
    val locationId: Long = 0,
    var reminderIdRefersTo: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val workedInsideDistanceArea: Boolean = false,
)
