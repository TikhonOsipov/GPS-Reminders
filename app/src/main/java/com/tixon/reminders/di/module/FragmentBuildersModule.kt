package com.tixon.reminders.di.module

import com.tixon.reminders.screen_reminders_list.AddReminderFragment
import com.tixon.reminders.screen_reminders_list.RemindersListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeRemindersListFragment(): RemindersListFragment

    @ContributesAndroidInjector
    abstract fun contributeAddReminderFragment(): AddReminderFragment
}
