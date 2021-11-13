package com.tixon.reminders.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица: Locations
 *
 * Местоположение, при нахождении рядом с которым возникает уведомление о напоминании [reminderId].
 *
 * @property locationId уникальный идентификатор местоположения
 * @property reminderId идентификатор напоминания, к которому относится местоположение
 * @property latitude ширина
 * @property longitude долгота
 */
@Entity(
    tableName = "Locations",
)
class LocationDb(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int,
    val reminderId: Int,
    val latitude: Double,
    val longitude: Double,
)
