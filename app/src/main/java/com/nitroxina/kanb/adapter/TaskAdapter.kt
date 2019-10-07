package com.nitroxina.kanb.adapter

import android.annotation.TargetApi
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
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.MainActivity
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.viewmodel.EditTaskViewModel

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
//                card.strokeColor = TaskColor.GRAY
//                card.strokeWidth = 3
                card.setOnClickListener {
                    val context = card.context
                    if (context is MainActivity) {
                        context.navigateTo(MainActivity.TASK_DETAIL_FRAGMENT, toBundle(task))
                    }
                }

                val buttonEdit = findViewById<MaterialButton>(com.nitroxina.kanb.R.id.edit_task_button)
                buttonEdit.setOnClickListener {
                    openTaskForEdition(holder.taskItemView, task)
                }
                val ownerIcon = findViewById<ImageView>(com.nitroxina.kanb.R.id.icon_owner)
                ownerIcon.setImageResource(com.nitroxina.kanb.R.drawable.circle_bg)
                val textOwnerIcon = findViewById<TextView>(com.nitroxina.kanb.R.id.icon_text)
                textOwnerIcon.text = profile.username!!.substring(0..1).toUpperCase()
                textOwnerIcon.bringToFront()
                findViewById<MaterialTextView>(com.nitroxina.kanb.R.id.owner_name).text = profile.username
            }
        }
    }

    private fun openTaskForEdition(taskItemView: ViewGroup, task: Task) {
        val context = taskItemView.context
        if (context is MainActivity) {
            val taskViewModel = ViewModelProviders.of(context).get(EditTaskViewModel::class.java)
            taskViewModel.dataTask.value = task
            EditTaskDialogFragment().show(context.supportFragmentManager, "edit_dialog")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(com.nitroxina.kanb.R.layout.task_list_item_layout, null)
        return TaskViewHolder(itemView)
    }

    class TaskViewHolder(val taskItemView: View) : RecyclerView.ViewHolder(taskItemView)

    fun toBundle(task: Task) : Bundle {
        val bundle = Bundle()
        bundle.putSerializable("task", task)
        return bundle
    }

}