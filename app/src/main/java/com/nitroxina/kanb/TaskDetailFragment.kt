package com.nitroxina.kanb

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.extensions.toUser
import com.nitroxina.kanb.kanboardApi.GET_USER
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.online.KBResponse
import io.noties.markwon.Markwon
import org.json.JSONObject
import java.lang.ref.WeakReference

class TaskDetailFragment : Fragment(){

        private lateinit var rootView: View

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View {
            rootView = inflater.inflate(R.layout.task_detail_layout, null)
            val task = arguments?.get("task") as Task

            task?.let {
                CreatorNameTaskAsyncTask(activity!!, it).execute()
                updateView(it)
            }
            return rootView
        }

     private fun updateView(task: Task) {
        this.rootView.apply {
            val cardView = findViewById<MaterialCardView>(R.id.task_detail_card)
            cardView.strokeColor = Color.parseColor(TaskColor.hexaBorderColorOf(task.color_id!!))
            cardView.strokeWidth = 4
            cardView.setCardBackgroundColor(Color.parseColor(TaskColor.hexaBackgroundColorOf(task.color_id!!)))

            findViewById<TextView>(R.id.task_title).text = task.title
            findViewById<TextView>(R.id.task_status_value).text = task.is_active
            findViewById<TextView>(R.id.task_assignee_value).text = task.assignee_name

            findViewById<TextView>(R.id.task_priority_value).text = task.priority?.toString()
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

    private fun updateNameOfCreator(name: String?) {
        this.rootView.apply {
            findViewById<TextView>(R.id.task_creator_value).text = name
        }

    }

    // TO navigation works
    override fun onResume() {
        if(activity!! is MainActivity) {
            (activity!! as MainActivity).actualFragment = MainActivity.TASK_DETAIL_FRAGMENT
        }
        super.onResume()
    }

    private inner class CreatorNameTaskAsyncTask internal constructor(context: Context, val task: Task) : AsyncTask<Void, Void, KBResponse>() {
        private val activityReference: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): KBResponse {
            val parameters = "{\"user_id\": ${task.creator_id} }"
            return KBClient.execute(GET_USER, parameters)
        }

        override fun onPostExecute(result: KBResponse) {
            super.onPostExecute(result)
            val context = activityReference.get()!!
            if (!result.successful || result.conectionError != null) {
                updateNameOfCreator("")
            } else {
                updateNameOfCreator(JSONObject(result.result).toUser().name.toString())
            }
        }
    }

}