package com.nitroxina.kanb.adapter

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.*
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.extensions.toBoard
import com.nitroxina.kanb.extensions.toProject
import org.json.JSONArray
import org.json.JSONObject

class ProjectAdapter(val profile: Profile, val list: MutableList<Project>) :  RecyclerView.Adapter<ProjectAdapter.ProjecViewHolder>() {

    lateinit var listener: AdapterView.OnItemClickListener
    lateinit var progressBar: ProgressBar

    init {
        //loadList()
    }

    //private var list: MutableList<Project> = mutableListOf()

    fun loadList() {
        loadList { }
    }

    fun loadList(callback: ()->Unit) {
        object: AsyncTask<Void, Void, Unit>(){

            override fun onPreExecute() {
                super.onPreExecute()
                if(this@ProjectAdapter::progressBar.isInitialized) {
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun doInBackground(vararg params: Void?) {
                val kbResponse = KBClient.execute(GET_MY_PROJECTS)
                val jsonArray = JSONArray(kbResponse.result)
                val userId = this@ProjectAdapter.profile.id
                for(i in 1..jsonArray.length()){
                    val project = (jsonArray[i-1] as JSONObject).toProject()
                    val role = loadRole(project.id, userId)
                    project.projectRole = role
                    project.board = loadBoard(project.id)
                    list.add(project)

                }


//                var keysList = jsonObject.keys()
//                this@ProjectAdapter.list = mutableListOf<Project>()
//                keysList.forEach {
//                    val id = it
//                    val userId = this@ProjectAdapter.profile.id
//                    val projectRole = loadRole(id, userId)
//                    val name = jsonObject.getString(id)
//                    val project = Project(id, name)
//                    project.projectRole = projectRole
//                    project.board = loadBoard(id)
//                    list.add(project)
//                }
            }
            override fun onPostExecute(result: Unit?) {
                this@ProjectAdapter.notifyDataSetChanged()
                if(this@ProjectAdapter::progressBar.isInitialized) {
                    progressBar.visibility = View.GONE
                }
                callback()
            }
        }.execute()
    }

    private fun loadRole(projectId: String?, userId: String) : KBProjectRole? {
        val parameters = "[$projectId, $userId]"
        val kbResponse = KBClient.execute(GET_PROJECT_USER_ROLE, parameters)
        if(!kbResponse.successful) {
            // TODO: Se deu erro tratar aqui
        }
        return KBProjectRole.getKBRoleByValue(kbResponse.result!!)
    }

    private fun loadBoard(projectId: String?) : Board? {
        val parameters = "[ $projectId ]"
        val kbResponse = KBClient.execute(GET_BOARD, parameters)
        val jsonArray = JSONArray(kbResponse.result)
        return jsonArray.toBoard()
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: ProjecViewHolder, position: Int) {
        if(this.list != null && !this.list.isEmpty()){
            (holder.projectItemView as ViewGroup).apply {
                val project = this@ProjectAdapter.list[position]
                findViewById<TextView>(R.id.project_name).text = project.name
                findViewById<TextView>(R.id.project_id).text = "#${project.id}"

                if(!project.isPrivate()) {
                    findViewById<ImageView>(R.id.lock_image).visibility = View.GONE
                }

                if(project.board != null) {
                    findViewById<TextView>(R.id.columns_task_information).text = project.board!!.boardResume()
                }
                val projectCard = findViewById<MaterialCardView>(R.id.project_card)
                projectCard.strokeColor = Color.GRAY
                projectCard.strokeWidth = 3
                projectCard.setOnClickListener {
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