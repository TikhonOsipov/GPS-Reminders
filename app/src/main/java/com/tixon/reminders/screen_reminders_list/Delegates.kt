package com.tixon.reminders.screen_reminders_list

import android.app.Activity
import android.util.Log
import kotlin.reflect.KProperty

class preference(private val activity: Activity) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return activity.getPreferences().getFloat(property.name, 0f).toDouble().also { value ->
            Log.d("myLogs_pref_delegate", "obtaining value '$value' from preferences by key '${property.name}'")
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        Log.d("myLogs_pref_delegate", "saving value '$value' into preferences by key '${property.name}'")
        activity.getPreferences()
            .edit()
            .putFloat(
                property.name,
                value.toFloat()
            )
            .apply()
    }
}