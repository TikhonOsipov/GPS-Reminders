package com.tixon.reminders.storage

import com.tixon.reminders.model.Reminder
import io.reactivex.Observable

interface RemindersRepository {

    fun getReminders(): Observable<List<Reminder>>
}
