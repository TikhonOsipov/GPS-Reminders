package com.tixon.reminders.di.module

import android.content.Context
import androidx.room.Room
import com.tixon.reminders.storage.GpsRemindersDatabase
import com.tixon.reminders.storage.RemindersRepository
import com.tixon.reminders.storage.RemindersRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    companion object {
        private const val DATABASE_NAME = "GpsRemindersDatabase"
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(
        context: Context
    ): GpsRemindersDatabase = Room.databaseBuilder(
        context,
        GpsRemindersDatabase::class.java,
        DATABASE_NAME
    )
        .build()

    @Provides
    @Singleton
    fun provideRemindersRepository(
        repository: RemindersRepositoryImpl): RemindersRepository = repository
}
