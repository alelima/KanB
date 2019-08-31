package com.nitroxina.kanb.adapter

import android.content.Intent
import android.os.AsyncTask
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.kanboardApi.GET_MY_PROJECTS_LIST
import com.nitroxina.kanb.kanboardApi.GET_PROJECT_USER_ROLE
import com.nitroxina.kanb.kanboardApi.KBRole
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.online.KBClient
import org.json.JSONArray
import org.json.JSONObject

class ProjectAdapter(val profile: Profile) :  RecyclerView.Adapter<ProjectAdapter.ProjecViewHolder>() {

    lateinit var listener: AdapterView.OnItemClickListener

    init {
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
                    val userId = this@ProjectAdapter.profile.id
                    val role = loadRole(id, userId)
                    //pegar a role do projeto aqui id projeto + id user
                    val name = jsonObject.getString(id)
                    val project = Project(id, name)
                    project.role = role
                    list.add(project)
                }
            }
            override fun onPostExecute(result: Unit?) {
                this@ProjectAdapter.notifyDataSetChanged()
                callback()
            }
        }.execute()
    }

    private fun loadRole(id: String?, userId: String) : KBRole? {
        val parameters = "[$id, $userId]"
        val kbResponse = KBClient.execute(GET_PROJECT_USER_ROLE, parameters)
        return KBRole.getKBRoleByValue(kbResponse.result)
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