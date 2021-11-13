package com.tixon.reminders.model

class Reminder(
    val reminderId: Int,
    val listId: Int?,
    val title: String,
    val isCompleted: Boolean
)
