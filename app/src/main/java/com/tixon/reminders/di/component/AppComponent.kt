package com.tixon.reminders.di.component

import android.app.Application
import com.tixon.reminders.app.App
import com.tixon.reminders.di.module.ActivityBuilderModule
import com.tixon.reminders.di.module.AppModule
import com.tixon.reminders.di.module.DatabaseModule
import com.tixon.reminders.di.module.ViewModelFactoryModule
import com.tixon.reminders.storage.RemindersRepository
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

    fun remindersRepository(): RemindersRepository

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
