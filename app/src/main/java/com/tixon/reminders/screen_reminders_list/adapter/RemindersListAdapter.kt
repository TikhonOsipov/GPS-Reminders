package com.tixon.reminders.screen_reminders_list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tixon.reminders.items.ReminderItemView

class RemindersListAdapter<T: Row> : RecyclerView.Adapter<RemindersListAdapter<T>.ViewHolder>() {

    private val remindersList: MutableList<T> = mutableListOf()

    var onItemClick: ((String) -> Unit)? = null

    fun setData(data: List<T>) {
        remindersList.apply {
            clear()
            addAll(data)
        }
        notifyItemRangeInserted(0, data.size-1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ReminderItemViewHolder(
            view = ReminderItemView(
                context = parent.context
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = remindersList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return remindersList.size
    }

    inner class ReminderItemViewHolder(val view: ReminderItemView) : ViewHolder(view) {
        override fun bind(item: Row) {
            item as ReminderRow
            itemView as ReminderItemView
            itemView.setData(item.data)

            view.setOnClickListener {
                onItemClick?.invoke(item.data.title)
            }
        }
    }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: Row)
    }
}