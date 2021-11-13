package com.tixon.reminders.di.component

import android.app.Application
import android.content.Context
import com.tixon.reminders.app.App
import com.tixon.reminders.di.module.ActivityBuilderModule
import com.tixon.reminders.di.module.AppModule
import com.tixon.reminders.di.module.DatabaseModule
import com.tixon.reminders.di.module.ViewModelFactoryModule
import com.tixon.reminders.storage.GpsRemindersDatabase
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilderModule::class,
        ViewModelFactoryModule::class,
        DatabaseModule::class,
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
