package com.tixon.reminders.di.module

import androidx.lifecycle.ViewModel
import com.tixon.reminders.di.ViewModelKey
import com.tixon.reminders.screen_reminders_list.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMyViewModel(viewModel: MainViewModel): ViewModel
}
