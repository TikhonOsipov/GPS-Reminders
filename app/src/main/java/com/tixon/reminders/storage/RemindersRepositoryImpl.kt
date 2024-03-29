package com.tixon.reminders.storage

import android.util.Log
import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.model.Reminder
import com.tixon.reminders.model.prepareForDb
import com.tixon.reminders.model.prepareToDb
import com.tixon.reminders.storage.database.GpsRemindersDatabase
import com.tixon.reminders.storage.entity.LocationDb
import com.tixon.reminders.storage.entity.ReminderWithLocations
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Тест: @see [RemindersRepositoryTest]
 */
class RemindersRepositoryImpl
@Inject constructor(
    private val database: GpsRemindersDatabase,
) : RemindersRepository {

    /**
     * Юзер может добавлять местоположения на карту, рядом с которыми
     *  будущее напоминание должно срабатывать.
     *
     * Пока напоминание не создано, эти местоположения записываются в список [pendingLocations].
     * Он очищается при выходе с экрана или при создании напоминания.
     *
     * Может быть очищен извне через [clearPendingLocationList].
     */
    private val pendingLocations = mutableListOf<PlaceLocation>()

    override fun getReminders(): Observable<List<Reminder>> {
        return database.reminders()
            .getRemindersList()
            .map { list -> list.map(::mapReminder) }
    }

    override fun getReminderById(reminderId: Long): Observable<Reminder> {
        return database.reminders()
            .getReminderById(reminderId = reminderId)
            .map(::mapReminder)
    }

    private fun mapReminder(reminderFromDb: ReminderWithLocations): Reminder = with (reminderFromDb) {
        Reminder(
            reminderId = reminder.reminderId,
            listId = reminder.listId,
            title = reminder.title,
            isCompleted = reminder.isCompleted,
            locations = locations.map(::mapLocation),
        )
    }

    override fun addReminder(reminder: Reminder): Completable = Completable.fromCallable {
        database.reminders().insertReminderWithLocations(
            reminder = reminder.prepareToDb(),
            locations = reminder.locations.prepareForDb(),
        ) { locationsWithUpdatedIds ->
            database.locations().insertLocations(locationsWithUpdatedIds)
        }
            .also { pendingLocations.clear() }
    }

    override fun getLocations(): Observable<List<PlaceLocation>> {
        return database.locations()
            .getLocations()
            .map { list ->
                list.map(::mapLocation)
            }
    }

    private fun mapLocation(location: LocationDb): PlaceLocation = with(location) {
        PlaceLocation(
            id = locationId,
            latitude = latitude,
            longitude = longitude,
            reminderId = reminderIdRefersTo,
        )
    }

    override fun markLocationAsNotified(location: PlaceLocation) = Completable.fromCallable {
        Log.d("myLogs", "markLocationAsNotified: $location")
        database.locations().updateLocation(
            location = location.copy(workedInsideDistanceArea = true).prepareForDb()
        )
    }

    override fun markLocationNotifyReady(location: PlaceLocation) = Completable.fromCallable {
        Log.d("myLogs", "markLocationNotifyReady: $location")
        database.locations().updateLocation(
            location = location.copy(workedInsideDistanceArea = false).prepareForDb()
        )
    }

    override fun getPendingLocationsList(): List<PlaceLocation> {
        return pendingLocations
    }

    override fun addPendingLocation(place: PlaceLocation) {
        pendingLocations.add(place)
    }

    override fun removePendingLocation(place: PlaceLocation) {
        pendingLocations.removeAll { it == place }
    }

    override fun clearPendingLocationList() {
        pendingLocations.clear()
    }
}
