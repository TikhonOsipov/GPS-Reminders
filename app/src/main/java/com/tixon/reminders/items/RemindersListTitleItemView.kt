package com.tixon.reminders.items

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tixon.reminders.R
import com.tixon.reminders.extensions.inflate

class RemindersListTitleItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var tvTitle: TextView? = null

    init {
        this.inflate(R.layout.item_reminders_list_title)
        tvTitle = findViewById(R.id.tvListTitle)
    }

    fun setData(data: Data) {
        with(data) {
            tvTitle?.text = title
        }
    }

    data class Data(
        val title: String
    )
}
