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
import com.nitroxina.kanb.kanboardApi.CLOSE_TASK
import com.nitroxina.kanb.kanboardApi.UPDATE_TASK
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.online.KBResponse
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import org.joda.time.Days
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class TaskAdapter(val profile: Profile) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
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
                    loadTaskUsers(task)
                    list.add(task)
                }
            }
            override fun onPostExecute(result: Unit?) {
                this@TaskAdapter.notifyDataSetChanged()
                callback()
            }
        }.execute()
    }

    private fun loadTaskUsers(task: Task) {

    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if(this.list.isNotEmpty()){
            (holder.taskItemView as ViewGroup).apply {
                val task = this@TaskAdapter.list[position]
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_title).text = task.title
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_id).text = "#${task.id}"
                val colorLine : View = findViewById<View>(com.nitroxina.kanb.R.id.line_color)
                colorLine.setBackgroundColor(Color.parseColor(TaskColor.hexaBorderColorOf(task.color_id!!)))

                val card = findViewById<MaterialCardView>(com.nitroxina.kanb.R.id.task_card)
                @TargetApi(21)
                card.elevation = 4.0f
                card.setOnClickListener {
                    val context = card.context
                    if (context is MainActivity) {
                        context.navigateTo(MainActivity.TASK_DETAIL_FRAGMENT, task.toBundle())
                    }
                }

                val buttonOptions = findViewById<MaterialButton>(com.nitroxina.kanb.R.id.finalize_button)
                buttonOptions.setOnClickListener {
                    openOptions(holder.taskItemView, task, it)
                }

                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                val dateCreation = formatter.parse(task.date_creation)
                val dateNow = Date()
                val days = ((dateNow.time - dateCreation.time)/(1000*60*60*24))

                findViewById<TextView>(com.nitroxina.kanb.R.id.task_days).text = days.toString() +"d"

                val ownerIcon = findViewById<ImageView>(com.nitroxina.kanb.R.id.icon_owner)
                ownerIcon.setImageResource(com.nitroxina.kanb.R.drawable.circle_bg)
                val textOwnerIcon = findViewById<TextView>(com.nitroxina.kanb.R.id.icon_text)
                textOwnerIcon.text = profile.username!!.substring(0..1).toUpperCase()
                textOwnerIcon.bringToFront()
                findViewById<MaterialTextView>(com.nitroxina.kanb.R.id.owner_name).text = profile.username
            }
        }
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