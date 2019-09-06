package com.nitroxina.kanb

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nitroxina.kanb.adapter.ProjectAdapter
import com.nitroxina.kanb.model.Profile

class ProjectListFragment : Fragment() {

    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.project_list_layout, null)
        profile = arguments?.get("profile") as Profile
        val projectAdapter = configureList(rootView)
        configureRefresh(rootView, projectAdapter)
        return rootView
    }

    private fun configureRefresh(rootView: View, projectAdapter: ProjectAdapter) {
        val refreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        refreshLayout.setOnRefreshListener {
            projectAdapter.loadList {
                refreshLayout.isRefreshing = false
            }
            true
        }
    }

    private fun configureList(rootView: View): ProjectAdapter {
        val viewManager = LinearLayoutManager(activity!!)
        val viewAdapter = ProjectAdapter(profile)
        val listView = rootView.findViewById<RecyclerView>(R.id.project_list)
        listView.layoutManager = viewManager
        listView.adapter = viewAdapter

        return viewAdapter
    }
}