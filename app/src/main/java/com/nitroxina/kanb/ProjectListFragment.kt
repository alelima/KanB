package com.nitroxina.kanb

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nitroxina.kanb.adapter.ProjectAdapter
import com.nitroxina.kanb.extensions.toBoard
import com.nitroxina.kanb.extensions.toProject
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.kanboardApi.GET_MY_PROJECTS
import com.nitroxina.kanb.kanboardApi.GET_PROJECT_USER_ROLE
import com.nitroxina.kanb.kanboardApi.KBProjectRole
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.online.KBClient
import org.json.JSONArray
import org.json.JSONObject

class ProjectListFragment : Fragment() {

    private lateinit var profile: Profile
    lateinit var progressBar: ProgressBar
    //var projectList: MutableList<Project> = mutableListOf()
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.project_list_layout, null)
        profile = arguments?.get("profile") as Profile
        //val projectAdapter = configureList(rootView)
        configureFAB(rootView)
        progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar)
        loadList {  }
        return rootView
    }

    private fun configureRefresh(rootView: View) {
        val refreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        refreshLayout.setOnRefreshListener {
            this.loadList {
                refreshLayout.isRefreshing = false
            }
        }
    }

    fun configureList(projectList: MutableList<Project>) {
        if(activity != null) {
            val viewManager = LinearLayoutManager(activity!!)
            val viewAdapter = ProjectAdapter(profile, projectList)
            val listView = rootView.findViewById<RecyclerView>(R.id.project_list)
            listView.layoutManager = viewManager
            listView.adapter = viewAdapter
        }
    }

    private fun configureFAB(rootView: View) {
        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab_project)
        fab.setOnClickListener {
            ProjectNewDialogFragment(profile.id).show(activity!!.supportFragmentManager, "create_dialog")
        }
    }

    fun loadList(callback: ()->Unit) {
        var projectList: MutableList<Project> = mutableListOf()
        object: AsyncTask<Void, Void, Unit>(){

            override fun onPreExecute() {
                super.onPreExecute()
                if(this@ProjectListFragment::progressBar.isInitialized) {
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun doInBackground(vararg params: Void?) {
                val kbResponse = KBClient.execute(GET_MY_PROJECTS)
                val jsonArray = JSONArray(kbResponse.result)
                val userId = this@ProjectListFragment.profile.id
                for(i in 1..jsonArray.length()){
                    val project = (jsonArray[i-1] as JSONObject).toProject()
                    val role = loadRole(project.id, userId)
                    project.projectRole = role
                    project.board = loadBoard(project.id)
                    projectList.add(project)
                }
            }
            override fun onPostExecute(result: Unit?) {
                if(this@ProjectListFragment::progressBar.isInitialized) {
                    progressBar.visibility = View.GONE
                }
                this@ProjectListFragment.configureList(projectList)
                configureRefresh(rootView)
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

    override fun onResume() {
        (activity!! as MainActivity).actualFragment = MainActivity.PROJECT_LIST_FRAGMENT
        super.onResume()
    }
}