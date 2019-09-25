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
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.MainActivity
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.scaleHeight
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import io.noties.markwon.Markwon

class TaskAdapter(val profile: Profile) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val expandHeight : Int = 1020

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
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_title).text = task.title
                findViewById<TextView>(com.nitroxina.kanb.R.id.task_id).text = "#${task.id}"
                findViewById<View>(com.nitroxina.kanb.R.id.line_color).setBackgroundColor(Color.parseColor(task.color_id))

//                val descriptionTxtView = findViewById<TextView>(com.nitroxina.kanb.R.id.descriptionView)
//                if(!descriptionTxtView.text.isNullOrBlank() && !task.description.isNullOrEmpty()) {
//                    Markwon.create(descriptionTxtView.context).setMarkdown(descriptionTxtView, task.description!!)
//                }
//                descriptionTxtView.visibility = View.GONE

                var minHeight = 0
                val card = findViewById<MaterialCardView>(com.nitroxina.kanb.R.id.task_card)
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
                    if (it.height == minHeight) { //if is collapsed
                        it.scaleHeight(expandHeight)
                        //descriptionTxtView.visibility = View.VISIBLE
                    } else {
                        //descriptionTxtView.visibility = View.GONE
                        it.scaleHeight(minHeight)
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

}