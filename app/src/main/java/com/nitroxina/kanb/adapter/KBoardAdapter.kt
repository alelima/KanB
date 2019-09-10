package com.nitroxina.kanb.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.allyants.boardview.SimpleBoardAdapter
import com.google.android.material.button.MaterialButton
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.MainActivity
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.KBRole
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import java.util.ArrayList

@Suppress("UNCHECKED_CAST")
class KBoardAdapter(context: Context?, data: ArrayList<KBColumn>, private val role: KBRole?, private val projectName: String) : SimpleBoardAdapter(context, data as ArrayList<SimpleColumn>) {

    override fun createItemView(context: Context, header_object: Any, item_object: Any,
        column_position: Int, item_position: Int): View {

        val itemView = View.inflate(context, R.layout.board_column_item_layout , null as ViewGroup?)

        val task = (this.columns[column_position] as Column).objects[item_position] as Task
        itemView.findViewById<TextView>(R.id.task_title).text = task.title
        itemView.findViewById<TextView>(R.id.task_id).text = task.id
        itemView.setBackgroundColor(Color.parseColor(task.color_id!!))
        itemView.background.alpha = 75
        return itemView
    }

    override fun createHeaderView(context: Context?, header_object: Any?, column_position: Int): View {
        val columnView = View.inflate(context, R.layout.board_column_layout, null as ViewGroup?)
        val column = (this.columns[column_position]
                as Column).header_object as com.nitroxina.kanb.model.Column
        columnView.findViewById<TextView>(R.id.column_title).text = column.title
        val addTaskButton = columnView.findViewById<MaterialButton>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            createNewTask(column, columnView)
        }
        //columnView.findViewById<Button>
        return columnView
    }

    override fun maxItemCount(column_position: Int): Int {
        return -1 // no size restriction
    }

    override fun isColumnLocked(column_position: Int): Boolean {
        if(role == KBRole.MANAGER) {
            return false
        }
        return true
    }

    override fun isItemLocked(column_position: Int): Boolean {
        if(role == KBRole.VIEWER) {
            return true
        }
        return false
    }

    override fun createFooterView(context: Context?, footer_object: Any?, column_position: Int): View? {
        return null
    }

    override fun createFooterObject(column_position: Int): Any? {
        return null
    }

    private fun createNewTask(column: com.nitroxina.kanb.model.Column, columnView: View) {
        val context = columnView.context
        if (context is BoardActivity) {
            var task = Task("", column.project_id!!)
            task.column_id = column.id
            task.project_name = projectName
            val taskViewModel = ViewModelProviders.of(context).get(EditTaskViewModel::class.java)
            taskViewModel.dataTask.value = task
            EditTaskDialogFragment().show(context.supportFragmentManager, "edit_dialog")
        }
    }

    class KBColumn(column: com.nitroxina.kanb.model.Column, items: ArrayList<Any>) : Column() {
        init {
            this.header_object = column
            this.objects = items
        }
    }
}