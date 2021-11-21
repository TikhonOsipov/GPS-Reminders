package com.tixon.reminders.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица: Reminders
 *
 * @property reminderId уникальный идентификатор напоминания
 * @property listId если напоминание принадлежит какому-то списку, в этом поле хранится id списка
 * @property title название напоминания
 * @property isCompleted статус выполнено/не выполнено
 */
@Entity(tableName = "Reminders")
class ReminderDb(
    @PrimaryKey(autoGenerate = true)
    val reminderId: Long = 0,
    val listId: Long? = null,
    val title: String,
    val isCompleted: Boolean
)
