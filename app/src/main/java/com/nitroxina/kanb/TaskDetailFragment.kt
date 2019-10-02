package com.nitroxina.kanb

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.model.Task
import io.noties.markwon.Markwon

class TaskDetailFragment : Fragment(){

        private lateinit var rootView: View

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            rootView = inflater.inflate(R.layout.task_detail_layout, null)
            val task = arguments?.get("task") as Task
            task?.let { updateView(it) }
            return rootView
        }

    private fun updateView(task: Task) {
        this.rootView.apply {
            val cardView = findViewById<MaterialCardView>(R.id.task_detail_card)
            cardView.strokeColor = Color.parseColor(task.color_id)
            cardView.strokeWidth = 4
            cardView.setCardBackgroundColor(Color.parseColor(task.color_id))
            cardView.background.alpha = 75

            findViewById<TextView>(R.id.task_title).text = task.title
            findViewById<TextView>(R.id.task_status_value).text = task.is_active
            findViewById<TextView>(R.id.task_assignee_value).text = task.assignee_name
            findViewById<TextView>(R.id.task_priority_value).text = task.priority?.toString()
            findViewById<TextView>(R.id.task_creator_value).text = task.creator_name
            findViewById<TextView>(R.id.task_swimlane_value).text = task.swimlane_name
            findViewById<TextView>(R.id.task_column_value).text = task.column_name
            findViewById<TextView>(R.id.task_position_value).text = task.position
            findViewById<TextView>(R.id.task_date_started_value).text = task.date_started
            findViewById<TextView>(R.id.task_date_created_value).text = task.date_creation
            findViewById<TextView>(R.id.task_date_modified_value).text = task.date_modification
            findViewById<TextView>(R.id.task_date_moved_value).text = task.date_moved

            //
            val descriptionTxtView = findViewById<TextView>(R.id.description_value)
            if(!task.description.isNullOrEmpty()) {
                Markwon.create(descriptionTxtView.context).setMarkdown(descriptionTxtView, task.description!!)
            }
        }
    }

}