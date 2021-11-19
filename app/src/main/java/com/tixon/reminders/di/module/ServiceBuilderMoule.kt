package com.tixon.reminders.di.module

import com.tixon.reminders.service.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderMoule {

    @ContributesAndroidInjector
    abstract fun contributeLocationService(): LocationService
}
