package com.tixon.reminders.di.module

import com.tixon.reminders.screen_reminders_list.MainActivity
import com.tixon.reminders.service.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(
        modules = [FragmentBuildersModule::class, ViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}
