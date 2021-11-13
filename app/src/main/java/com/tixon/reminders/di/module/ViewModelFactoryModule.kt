package com.tixon.reminders.di.module

import androidx.lifecycle.ViewModelProvider
import com.tixon.reminders.di.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
