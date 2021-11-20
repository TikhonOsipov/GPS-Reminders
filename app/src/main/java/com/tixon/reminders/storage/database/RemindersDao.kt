package com.tixon.reminders.storage.database

import androidx.room.*
import com.tixon.reminders.storage.entity.LocationDb
import com.tixon.reminders.storage.entity.ReminderDb
import com.tixon.reminders.storage.entity.ReminderWithLocations
import io.reactivex.Observable

@Dao
interface RemindersDao {

    @Transaction
    @Query("select * from Reminders")
    fun getRemindersList(): Observable<List<ReminderWithLocations>>

    @Transaction
    @Query("select * from Reminders where reminderId=:reminderId")
    fun getReminderById(reminderId: Long): Observable<ReminderWithLocations>

    @Insert
    fun insertReminder(reminder: ReminderDb): Long

    @Transaction
    fun insertReminderWithLocations(reminder: ReminderDb, locations: List<LocationDb>, insertLocationsLambda: (List<LocationDb>) -> Unit) {
        val idOfInsertedReminder = insertReminder(reminder)
        insertLocationsLambda.invoke(
            locations.map {
                it.copy(
                    reminderIdRefersTo = idOfInsertedReminder,
                )
            }
        )
    }

    @Update
    fun updateReminder(reminder: ReminderDb)

    @Query("delete from Reminders where reminderId=:reminderId")
    fun removeReminder(reminderId: Int)

    @Query("delete from Reminders")
    fun removeAllReminders()
}
