package com.tixon.reminders.app

import android.content.Context
import com.tixon.reminders.di.component.AppComponent
import com.tixon.reminders.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    companion object {
        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    lateinit var appComponent: AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
            .also { appComponent = it }
    }
}