package com.tixon.reminders.screen_reminders_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tixon.reminders.R
import com.tixon.reminders.items.ReminderItemView
import com.tixon.reminders.screen_reminders_list.adapter.ReminderRow
import com.tixon.reminders.screen_reminders_list.adapter.RemindersListAdapter
import com.tixon.reminders.screen_reminders_list.adapter.Row
import com.tixon.reminders.util.WrapContentLinearLayoutManager
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class RemindersListFragment : DaggerFragment() {

    companion object {
        private const val REMINDER_ROW_ID = "reminderRow"
    }

    private lateinit var rv: RecyclerView
    private lateinit var fab: FloatingActionButton

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewmodel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(RemindersListViewModel::class.java)
    }

    private val adapter = RemindersListAdapter<Row>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_reminders_list, container, false)
        .apply {
            rv = findViewById(R.id.rvReminders)
            rv.layoutManager = WrapContentLinearLayoutManager(context)
            rv.adapter = adapter

            fab = findViewById(R.id.fabReminders)
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_RemindersList_to_AddReminder)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.remindersLiveData.observe(viewLifecycleOwner, { remindersList ->
            adapter.setData(remindersList.map { reminder ->
                ReminderRow(
                    id = REMINDER_ROW_ID,
                    hashCode = hashCode(),
                    data = ReminderItemView.Data(
                        title = reminder.title,
                        checked = reminder.isCompleted
                    )
                )
            })
        })

        viewmodel.load()
    }
}
