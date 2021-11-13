package com.tixon.reminders.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ReminderDb::class,
        LocationDb::class,
    ],
    version = 1
)
abstract class GpsRemindersDatabase : RoomDatabase() {

    abstract fun reminders(): RemindersDao
}
