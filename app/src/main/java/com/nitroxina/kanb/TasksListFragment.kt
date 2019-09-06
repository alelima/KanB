package com.nitroxina.kanb

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nitroxina.kanb.adapter.TaskAdapter
import com.nitroxina.kanb.viewmodel.EditTaskViewModel

class TasksListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.task_list_layout, null)
        val taskAdapter = configureList(rootView)
        configureRefresh(rootView, taskAdapter)
        configureListObserver()
        return rootView
    }

    private fun configureRefresh(rootView: View, taskAdapter: TaskAdapter) {
        val refreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        refreshLayout.setOnRefreshListener {
            taskAdapter.loadList {
                refreshLayout.isRefreshing = false
            }
            true
        }
    }

    private fun configureList(rootView: View): TaskAdapter {
        val viewManager = LinearLayoutManager(activity!!)
        val viewAdapter = TaskAdapter()
        val listView = rootView.findViewById<RecyclerView>(R.id.task_list)
        listView.layoutManager = viewManager
        listView.adapter = viewAdapter
        return viewAdapter
    }

    private fun configureListObserver() {
        val taskViewModel = ViewModelProviders.of(activity!!).get(EditTaskViewModel::class.java)
        taskViewModel.dataTask.observe(this, Observer {
            onResume()
        })
    }

}