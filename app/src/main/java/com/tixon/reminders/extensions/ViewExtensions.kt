package com.tixon.reminders.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(
    @LayoutRes layoutResource: Int
): View {
    return View.inflate(context, layoutResource, this)
}