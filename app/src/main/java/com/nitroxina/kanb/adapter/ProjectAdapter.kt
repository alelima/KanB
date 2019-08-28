package com.nitroxina.kanb.adapter

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.GET_MY_PROJECTS_LIST
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.online.KBClient
import org.json.JSONObject

class ProjectAdapter :  RecyclerView.Adapter<ProjectAdapter.ProjecViewHolder> {

    lateinit var listener: AdapterView.OnItemClickListener

    constructor() : super(){
        loadList()
    }

    private var list: MutableList<Project> = mutableListOf()

    fun loadList() {
        loadList { }
    }

    fun loadList(callback: ()->Unit) {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val kbResponse = KBClient.execute(GET_MY_PROJECTS_LIST)
                val jsonObject = JSONObject(kbResponse.result)
                var keysList = jsonObject.keys()
                this@ProjectAdapter.list = mutableListOf<Project>()
                keysList.forEach {
                    val id = it
                    val name = jsonObject.getString(id)
                    val project = Project(id, name)
                    list.add(project)
                }
            }
            override fun onPostExecute(result: Unit?) {
                this@ProjectAdapter.notifyDataSetChanged()
                callback()
            }
        }.execute()
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: ProjecViewHolder, position: Int) {
        if(this.list != null && !this.list.isEmpty()){
            (holder.projectItemView as ViewGroup).apply {
                val project = this@ProjectAdapter.list[position]
                findViewById<TextView>(R.id.project_name).text = project.name
                findViewById<TextView>(R.id.project_id).text = project.id
                holder.projectItemView.setOnClickListener {
                    val context = holder.projectItemView.context
                    val intent = Intent(context, BoardActivity::class.java)
                    intent.putExtra("project", project)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjecViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.project_list_item_layout, null)
        return ProjecViewHolder(itemView)
    }

    class ProjecViewHolder(val projectItemView: View) : RecyclerView.ViewHolder(projectItemView)

}