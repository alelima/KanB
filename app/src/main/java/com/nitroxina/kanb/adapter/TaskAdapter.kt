package com.nitroxina.kanb.adapter

import android.annotation.TargetApi
import android.graphics.Color
import android.os.AsyncTask
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.kanboardApi.GET_MY_DASHBOARD
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.toTask
import org.json.JSONArray
import org.json.JSONObject
import android.util.DisplayMetrics
import android.content.Context
import android.view.WindowManager
import android.view.ViewTreeObserver
import com.nitroxina.kanb.scaleView

class TaskAdapter(context: Context?) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val windowHeight : Int

    init {
        val windowmanager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dimension = DisplayMetrics()
        windowmanager.defaultDisplay.getMetrics(dimension)
        windowHeight = dimension.heightPixels
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
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_title).text = task.title
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_id).text = task.id

                var minHeight = 0
                val card = findViewById<MaterialCardView>(com.nitroxina.kanb.R.id.task_card)
                card.setBackgroundColor(Color.parseColor(task.color_id!!))
                @TargetApi(21)
                card.elevation = 4.0f
                card.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            card.viewTreeObserver.removeOnPreDrawListener(this)
                            minHeight = card.height
                            val layoutParams = card.layoutParams
                            layoutParams.height = minHeight
                            card.layoutParams = layoutParams
                            return true
                        }
                })
                card.setOnClickListener {
                    it as MaterialCardView
                    if (it.height === minHeight) { //if is collapsed
                        it.scaleView(windowHeight)
                    } else {
                        it.scaleView(minHeight)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(com.nitroxina.kanb.R.layout.task_list_item_layout, null)
        return TaskViewHolder(itemView)
    }

    class TaskViewHolder(val taskItemView: View) : RecyclerView.ViewHolder(taskItemView)

}