package com.tixon.reminders.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tixon.reminders.storage.entity.LocationDb
import io.reactivex.Observable

@Dao
interface LocationsDao {

    @Query("select * from Locations where reminderIdRefersTo=:reminderId")
    fun getLocationsForReminder(reminderId: Int): Observable<List<LocationDb>>

    @Insert
    fun insertLocations(locations: List<LocationDb>)

    @Update
    fun updateLocation(location: LocationDb)

    @Query("delete from Locations where locationId=:locationId")
    fun removeLocation(locationId: Int)

    @Query("delete from Locations where reminderIdRefersTo=:reminderId")
    fun removeLocationsForReminder(reminderId: Int)

    @Query("delete from Locations")
    fun removeAllLocations()
}
