package com.nitroxina.kanb.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.allyants.boardview.SimpleBoardAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.KBProjectRole
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import java.util.ArrayList

@Suppress("UNCHECKED_CAST")
class KBoardAdapter(context: Context?, data: ArrayList<KBColumn>, private val projectRole: KBProjectRole?, private val project: Project) : SimpleBoardAdapter(context, data as ArrayList<SimpleColumn>) {

    val columnsTaskCount = arrayListOf<Pair<TextView, String?>>()

    override fun createItemView(context: Context, header_object: Any, item_object: Any,
        column_position: Int, item_position: Int): View {

        val itemView = View.inflate(context, R.layout.board_column_item_layout , null as ViewGroup?)

        val task = (this.columns[column_position] as Column).objects[item_position] as Task
        val cardView = itemView.findViewById<MaterialCardView>(R.id.board_item_card)

        itemView.findViewById<TextView>(R.id.task_title).text = task.title
        itemView.findViewById<TextView>(R.id.task_id).text = task.id
        cardView.strokeColor = Color.parseColor(TaskColor.hexaBorderColorOf(task.color_id!!))
        cardView.strokeWidth = 4
        cardView.setCardBackgroundColor(Color.parseColor(TaskColor.hexaBackgroundColorOf(task.color_id!!)))
        cardView.setOnClickListener {
            val context = cardView.context
            if (context is BoardActivity) {
                context.openDetailTask(task.toBundle())
            }
        }
        return itemView
    }

    override fun createHeaderView(context: Context?, header_object: Any?, column_position: Int): View {
        val columnView = View.inflate(context, R.layout.board_column_layout, null as ViewGroup?)
        val boardColumn = this.columns[column_position] as Column
        val column = boardColumn.header_object as com.nitroxina.kanb.model.Column
        columnView.findViewById<TextView>(R.id.column_title).text = column.title
        val colmNuberTask = columnView.findViewById<TextView>(R.id.number_tasks)
        colmNuberTask.text = "(${boardColumn.objects.size}" +
                if(!column.task_limit.isNullOrEmpty() && column.task_limit != "0") "/${column.task_limit}" else {""} + ")"
        columnsTaskCount.add(colmNuberTask to column.task_limit)
        val addTaskButton = columnView.findViewById<MaterialButton>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            createNewTask(column, columnView)
        }
        return columnView
    }

    override fun maxItemCount(column_position: Int): Int {
        return -1 // no size restriction
    }

    override fun isColumnLocked(column_position: Int): Boolean {
        if(projectRole == KBProjectRole.MANAGER) {
            return false
        }
        return true
    }

    override fun isItemLocked(column_position: Int): Boolean {
        if(projectRole == KBProjectRole.VIEWER) {
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
            task.project_name = project.name
            val taskViewModel = ViewModelProviders.of(context).get(EditTaskViewModel::class.java)
            taskViewModel.dataTask.value = task
            EditTaskDialogFragment().show(context.supportFragmentManager, "edit_dialog")
        }
    }

    fun updateColumnHeadCount(startColumn: Int, endColumn: Int) {
        var (startCol, startTaskLimit) = columnsTaskCount[startColumn]
        var (endCol, endTaskLimit) = columnsTaskCount[endColumn]
        val boardColumnStart = this.columns[startColumn] as Column
        val boardColumnEnd = this.columns[endColumn] as Column
        startCol.text = "(${boardColumnStart.objects.size}" +
                if(!startTaskLimit.isNullOrEmpty() && startTaskLimit != "0") "/${startTaskLimit}" else {""} + ")"
        endCol.text = "(${boardColumnEnd.objects.size}" +
                if(!endTaskLimit.isNullOrEmpty() && endTaskLimit != "0") "/${endTaskLimit}" else {""} + ")"
    }

    class KBColumn(column: com.nitroxina.kanb.model.Column, items: ArrayList<Any>) : Column() {
        init {
            this.header_object = column
            this.objects = items
        }
    }
}