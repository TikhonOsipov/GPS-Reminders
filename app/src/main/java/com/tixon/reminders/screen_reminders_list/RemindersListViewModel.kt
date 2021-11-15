package com.tixon.reminders.screen_reminders_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tixon.reminders.model.Reminder
import com.tixon.reminders.storage.RemindersRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RemindersListViewModel
@Inject constructor(
    private val remindersRepository: RemindersRepository,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val reminders = MutableLiveData<List<Reminder>>()

    val remindersLiveData: LiveData<List<Reminder>>
        get() = reminders

    fun load() {
        remindersRepository.getReminders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { remindersList ->
                    reminders.value = remindersList
                },
                onError = {
                    it.printStackTrace()
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
