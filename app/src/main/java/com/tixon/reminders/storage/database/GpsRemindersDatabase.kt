package com.tixon.reminders.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tixon.reminders.storage.entity.LocationDb
import com.tixon.reminders.storage.entity.ReminderDb

@Database(
    entities = [
        ReminderDb::class,
        LocationDb::class,
    ],
    version = 1
)
abstract class GpsRemindersDatabase : RoomDatabase() {

    abstract fun reminders(): RemindersDao

    abstract fun locations(): LocationsDao
}
