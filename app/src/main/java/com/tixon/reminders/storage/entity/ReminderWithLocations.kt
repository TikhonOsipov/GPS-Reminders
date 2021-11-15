package com.tixon.reminders.storage.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.tixon.reminders.storage.entity.LocationDb
import com.tixon.reminders.storage.entity.ReminderDb

/**
 * Объединяет таблицы [ReminderDb] и [LocationDb], связь 1:*
 *
 * В [LocationDb] находится поле [LocationDb.reminderIdRefersTo], и если оно равно значению
 *  [ReminderDb.reminderId], то считается, что Location относится к Reminder.
 *
 * Parent: [ReminderDb]
 * Entity: [LocationDb]
 */
data class ReminderWithLocations(
    @Embedded
    val reminder: ReminderDb,
    @Relation(
        entity = LocationDb::class,
        parentColumn = "reminderId",
        entityColumn = "reminderIdRefersTo",
    )
    val locations: List<LocationDb>
)
