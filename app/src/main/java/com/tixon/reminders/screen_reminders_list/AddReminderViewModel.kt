package com.tixon.reminders.screen_reminders_list

import androidx.lifecycle.ViewModel
import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.model.Reminder
import com.tixon.reminders.storage.RemindersRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddReminderViewModel @Inject constructor(
    private val remindersRepository: RemindersRepository,
) : ViewModel() {

    fun addPlace(place: PlaceLocation) {
        remindersRepository.addPendingLocation(place)
    }

    fun removePlace(place: PlaceLocation) {
        remindersRepository.removePendingLocation(place)
    }

    fun getPendingLocations(): List<PlaceLocation> {
        return remindersRepository.getPendingLocationsList()
    }

    fun save(reminderTitle: String): Completable {
        val reminder = Reminder(
            title = reminderTitle,
            isCompleted = false,
            locations = remindersRepository.getPendingLocationsList()
        )
        return remindersRepository.addReminder(reminder)
            .subscribeOn(Schedulers.io())
    }

    override fun onCleared() {
        remindersRepository.clearPendingLocationList()
        super.onCleared()
    }
}
