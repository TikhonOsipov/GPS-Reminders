package com.tixon.reminders.storage

import com.tixon.reminders.model.Reminder
import io.reactivex.Observable
import javax.inject.Inject

class RemindersRepositoryImpl
@Inject constructor(
    private val database: GpsRemindersDatabase,
) : RemindersRepository {

    override fun getReminders(): Observable<List<Reminder>> {
        return database.reminders()
            .getRemindersList()
            .map { list ->
                list.map {
                    Reminder(
                        reminderId = it.reminderId,
                        listId = it.listId,
                        title = it.title,
                        isCompleted = it.isCompleted
                    )
                }
            }
    }
}
