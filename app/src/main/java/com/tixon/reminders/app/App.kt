package com.tixon.reminders.app

import com.tixon.reminders.di.component.AppComponent
import com.tixon.reminders.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    lateinit var appComponent: AppComponent
}