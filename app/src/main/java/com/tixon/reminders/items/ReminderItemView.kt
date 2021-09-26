package com.tixon.reminders.items

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tixon.reminders.R
import com.tixon.reminders.extensions.inflate

class ReminderItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var tvTitle: TextView? = null
    var radioButton: RadioButton? = null

    init {
        this.inflate(R.layout.item_reminder)
        tvTitle = findViewById(R.id.tvReminderTitle)
        radioButton = findViewById(R.id.reminderRadioButton)
    }

    fun setData(data: Data) {
        with(data) {
            tvTitle?.text = title
            radioButton?.isChecked = checked
        }
    }

    data class Data(
        val title: String,
        val checked: Boolean = false
    )
}