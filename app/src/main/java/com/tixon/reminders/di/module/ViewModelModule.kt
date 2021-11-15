package com.tixon.reminders.di.module

import androidx.lifecycle.ViewModel
import com.tixon.reminders.di.ViewModelKey
import com.tixon.reminders.screen_reminders_list.AddReminderViewModel
import com.tixon.reminders.screen_reminders_list.RemindersListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RemindersListViewModel::class)
    internal abstract fun bindRemindersListViewModel(viewModel: RemindersListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddReminderViewModel::class)
    internal abstract fun bindAddReminderViewModel(viewModel: AddReminderViewModel): ViewModel
}
