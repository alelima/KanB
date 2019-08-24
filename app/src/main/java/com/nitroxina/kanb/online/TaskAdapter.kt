package com.nitroxina.kanb.online

import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.GET_MY_DASHBOARD
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.toTask
import org.json.JSONArray
import org.json.JSONObject

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    constructor() : super(){
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
        if(this.list != null && !this.list.isEmpty()){
            (holder.taskItemView as ViewGroup).apply {
                val task = this@TaskAdapter.list[position]
                findViewById<TextView>(R.id.task_title).text = task.title
                findViewById<TextView>(R.id.task_id).text = task.id
                holder.taskItemView.setBackgroundColor(Color.parseColor(task.color_id!!))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_list_item_layout, null)
        return TaskViewHolder(itemView)
    }

    class TaskViewHolder(val taskItemView: View) : RecyclerView.ViewHolder(taskItemView)


}