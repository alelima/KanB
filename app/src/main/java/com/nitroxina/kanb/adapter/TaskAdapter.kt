package com.nitroxina.kanb.adapter

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.kanboardApi.GET_MY_DASHBOARD
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.extensions.toTask
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.MainActivity
import com.nitroxina.kanb.R
import com.nitroxina.kanb.extensions.generateInitials
import com.nitroxina.kanb.extensions.timePassedSince
import com.nitroxina.kanb.kanboardApi.CLOSE_TASK
import com.nitroxina.kanb.kanboardApi.UPDATE_TASK
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.online.KBResponse
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import kotlinx.android.synthetic.main.task_list_item_layout.view.*
import org.joda.time.Days
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class TaskAdapter(val profile: Profile) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var colorIconOwner: Int = 0
    private var ownerName: String = if (profile.name.isNullOrEmpty()) { profile.username } else { profile.name }
    private var initialsOwner: String = ownerName.generateInitials()

    init {
        loadList()
    }

    private var list: MutableList<Task> = mutableListOf()

    fun loadList() {
        loadList { }
    }

    fun loadList(callback: ()->Unit) {

        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val kbResponse = KBClient.execute(GET_MY_DASHBOARD)
                this@TaskAdapter.list = mutableListOf<Task>()
                val jsonList = JSONArray(kbResponse.result)
                for(i in 1..jsonList.length()){
                    val jsonObject = jsonList[i-1] as JSONObject
                    val task = jsonObject.toTask()
                    list.add(task)
                }
            }
            override fun onPostExecute(result: Unit?) {
                this@TaskAdapter.notifyDataSetChanged()
                callback()
            }
        }.execute()
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if(this.list.isNotEmpty()){
            (holder.taskItemView as ViewGroup).apply {
                val task = this@TaskAdapter.list[position]
                findViewById<TextView>(R.id.task_title).text = task.title
                findViewById<TextView>(R.id.task_id).text = "#${task.id}"
                val colorLine : View = findViewById<View>(R.id.line_color)
                colorLine.setBackgroundColor(Color.parseColor(TaskColor.hexaBorderColorOf(task.color_id!!)))

                val card = findViewById<MaterialCardView>(R.id.task_card)
                @TargetApi(21)
                card.elevation = 4.0f
                card.strokeColor = 0xFFF59362.toInt()
                card.strokeWidth = 2
                card.setOnClickListener {
                    val context = card.context
                    if (context is MainActivity) {
                        context.navigateTo(MainActivity.TASK_DETAIL_FRAGMENT, task.toBundle())
                    }
                }

                val buttonOptions = findViewById<MaterialButton>(R.id.finalize_button)
                buttonOptions.setOnClickListener {
                    openOptions(holder.taskItemView, task, it)
                }

                val days = Date().timePassedSince(task.date_creation!!)

                findViewById<TextView>(R.id.task_days).text = days.toString() +"d"
                findViewById<TextView>(R.id.task_priority_vl).text = "P${task.priority}"

                if(colorIconOwner == 0) {
                    val colors = resources.obtainTypedArray(R.array.mdcolor)
                    colorIconOwner = TaskColor.getRandomMaterialColor(colors)
                }
                val ownerIcon = findViewById<ImageView>(R.id.icon_owner)
                ownerIcon.setImageResource(R.drawable.circle_bg)
                ownerIcon.setColorFilter(colorIconOwner)

                val textOwnerIcon = findViewById<TextView>(R.id.icon_text)
                textOwnerIcon.text = initialsOwner
                textOwnerIcon.bringToFront()
                findViewById<MaterialTextView>(R.id.owner_name).text = ownerName

                findViewById<TextView>(R.id.breadcrumb).text = task.project_name + ">" + task.swimlane_name + ">" + task.column_name
            }
        }
    }

    private fun generateInitials() : String {
        var initials = ""
        val parts = ownerName.split(" ")
        initials = if(parts.size >= 2) {
             parts[0].substring(0..0).toUpperCase() + parts[1].substring(0..0).toUpperCase()
        } else {
            ownerName.substring(0..1).toUpperCase()
        }
        return initials
    }

    private fun openTaskForEdition(context: Context, task: Task) {
        if (context is MainActivity) {
            val taskViewModel = ViewModelProviders.of(context).get(EditTaskViewModel::class.java)
            taskViewModel.dataTask.value = task
            EditTaskDialogFragment().show(context.supportFragmentManager, "edit_dialog")
        }
    }

    private fun finalizeTaskOption(context: Context, task: Task) {
        AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.task_message_finalize))
            .setNeutralButton("Ok") { dialog, _ ->
                finalizeTask(context, task)
                dialog.dismiss()
            }
            .setNegativeButton("cancelar") {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun finalizeTask(context: Context, task: Task) {
        FinalizeTaskAsyncTask(context, task).execute()
    }

    private fun openOptions(taskItemView: ViewGroup, task: Task, view: View) {
        val context = taskItemView.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.task_menu_options_layout)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.task_opt_edit -> this.openTaskForEdition(context, task)
                R.id.task_opt_finalize -> this.finalizeTaskOption(context, task)
            }
            true
        }
        popupMenu.show()
        true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(com.nitroxina.kanb.R.layout.task_list_item_layout, null)
        return TaskViewHolder(itemView)
    }

    class TaskViewHolder(val taskItemView: View) : RecyclerView.ViewHolder(taskItemView)

    private class FinalizeTaskAsyncTask internal constructor(context: Context, val task: Task) : AsyncTask<Void, Void, KBResponse>() {
        private val activityReference: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): KBResponse {
            val parameters = task.toJsonCloseParameters()
            return KBClient.execute(CLOSE_TASK, parameters)
        }

        override fun onPostExecute(result: KBResponse) {
            super.onPostExecute(result)
            val context = activityReference.get()!!
            if (!result.successful || result.conectionError != null) {
                AlertDialog.Builder(context)
                    .setMessage("Ocorreu um erro, tente novamente")
                    .setNeutralButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
                return
            }
        }
    }
}