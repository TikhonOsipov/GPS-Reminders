package com.tixon.reminders.storage

import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.model.Reminder
import io.reactivex.Completable
import io.reactivex.Observable

interface RemindersRepository {

    fun getReminders(): Observable<List<Reminder>>

    fun getReminderById(reminderId: Long): Observable<Reminder>

    fun addReminder(reminder: Reminder): Completable

    fun getLocations(): Observable<List<PlaceLocation>>

    fun markLocationAsNotified(location: PlaceLocation): Completable

    fun markLocationNotifyReady(location: PlaceLocation): Completable

    fun getPendingLocationsList(): List<PlaceLocation>

    fun addPendingLocation(place: PlaceLocation)

    fun removePendingLocation(place: PlaceLocation)

    fun clearPendingLocationList()
}
