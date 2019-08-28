package com.nitroxina.kanb

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nitroxina.kanb.adapter.ProjectAdapter

class ProjectListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.project_list_layout, null)
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
        val viewAdapter = ProjectAdapter()
        val listView = rootView.findViewById<RecyclerView>(R.id.project_list)
        listView.layoutManager = viewManager
        listView.adapter = viewAdapter

        return viewAdapter
    }
}