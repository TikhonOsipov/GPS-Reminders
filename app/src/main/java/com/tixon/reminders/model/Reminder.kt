package com.tixon.reminders.model

import com.tixon.reminders.storage.entity.ReminderDb

class Reminder(
    val reminderId: Int? = null,
    val listId: Int? = null,
    val title: String,
    val isCompleted: Boolean,
    val locations: List<PlaceLocation>
)

fun Reminder.prepareToDb(): ReminderDb = with(this) {
    ReminderDb(
        title = title,
        isCompleted = isCompleted
    )
}
