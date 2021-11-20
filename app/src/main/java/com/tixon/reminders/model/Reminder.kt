package com.tixon.reminders.model

import com.tixon.reminders.storage.entity.ReminderDb

data class Reminder(
    val reminderId: Int? = null,
    val listId: Int? = null,
    val title: String,
    val isCompleted: Boolean,
    val locations: List<PlaceLocation>,
    val distance: Float = 0f
)

fun Reminder.prepareToDb(): ReminderDb = with(this) {
    ReminderDb(
        title = title,
        isCompleted = isCompleted
    )
}
