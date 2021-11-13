package com.tixon.reminders.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Observable

@Dao
interface RemindersDao {

    @Query("select * from Reminders")
    fun getRemindersList(): Observable<List<ReminderDb>>

    @Insert
    fun insertReminder(reminder: ReminderDb)

    @Update
    fun updateReminder(reminder: ReminderDb)

    @Query("delete from Reminders where reminderId=:reminderId")
    fun removeReminder(reminderId: Int)

    @Query("delete from Reminders")
    fun removeAllReminders()
}
