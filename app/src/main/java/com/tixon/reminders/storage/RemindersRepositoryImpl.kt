package com.tixon.reminders.storage

import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.model.Reminder
import com.tixon.reminders.model.prepareForDb
import com.tixon.reminders.model.prepareToDb
import com.tixon.reminders.storage.database.GpsRemindersDatabase
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
            .map { list ->
                list.map {
                    Reminder(
                        reminderId = it.reminder.reminderId,
                        listId = it.reminder.listId,
                        title = it.reminder.title,
                        isCompleted = it.reminder.isCompleted,
                        locations = it.locations.map { location ->
                            PlaceLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                reminderId = location.reminderIdRefersTo,
                            )
                        },
                    )
                }
            }
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
                list.map {
                    PlaceLocation(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        reminderId = it.reminderIdRefersTo,
                    )
                }
            }
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
