package com.tixon.reminders.screen_reminders_list.adapter

import com.tixon.reminders.items.ReminderItemView

sealed class Row(
    val id: String,
    val hashCode: Int
)

class ReminderRow(
    id: String,
    hashCode: Int,
    val data: ReminderItemView.Data
) : Row(id, hashCode)
