package com.nitroxina.kanb.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.allyants.boardview.SimpleBoardAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nitroxina.kanb.BoardActivity
import com.nitroxina.kanb.EditTaskDialogFragment
import com.nitroxina.kanb.R
import com.nitroxina.kanb.extensions.generateInitials
import com.nitroxina.kanb.extensions.timePassedSince
import com.nitroxina.kanb.kanboardApi.KBProjectRole
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import java.util.*

@Suppress("UNCHECKED_CAST")
class KBoardAdapter(context: Context?, data: ArrayList<KBColumn>, private val project: Project, var reloadFunction: ()-> Unit) : SimpleBoardAdapter(context, data as ArrayList<SimpleColumn>) {

    private val columnsTaskCount = arrayListOf<Pair<TextView, String?>>()
    private val colorsOwner  = arrayListOf<Pair<String, Int>>()

    override fun createItemView(context: Context, header_object: Any, item_object: Any,
        column_position: Int, item_position: Int): View {

        val itemView = View.inflate(context, R.layout.board_column_item_layout , null as ViewGroup?)
        itemView.apply {
           val task = (this@KBoardAdapter.columns[column_position] as Column).objects[item_position] as Task
            val cardView = itemView.findViewById<MaterialCardView>(R.id.board_item_card)

            findViewById<TextView>(R.id.task_title).text = task.title
            findViewById<TextView>(R.id.task_id).text = task.id
            findViewById<TextView>(R.id.name_owner).text = task.assignee_name
            findViewById<TextView>(R.id.creation_days).text = Date().timePassedSince(task.date_creation!!) + "d"
            findViewById<TextView>(R.id.priority).text = "P" + task.priority

            val ownerIcon = findViewById<ImageView>(R.id.owner_icon)
            val textOwnerIcon = findViewById<TextView>(R.id.icon_owner_text)
            if(!task.assignee_name.isNullOrEmpty()) {
                var colorIconOwner = 0
                colorsOwner.forEach {
                    if (it.first == task.assignee_name) {
                        colorIconOwner = it.second
                    }
                }

                if (colorIconOwner == 0) {
                    val colors = resources.obtainTypedArray(R.array.mdcolor)
                    colorIconOwner = TaskColor.getRandomMaterialColor(colors)
                    colorsOwner.add(task.assignee_name!! to colorIconOwner)
                }


                ownerIcon.setImageResource(R.drawable.circle_bg)
                ownerIcon.setColorFilter(colorIconOwner)


                textOwnerIcon.text = task.assignee_name?.generateInitials()
                textOwnerIcon.bringToFront()
            } else {
                ownerIcon.visibility = View.GONE
                textOwnerIcon.visibility = View.GONE
            }

            cardView.strokeColor = Color.parseColor(TaskColor.hexaBorderColorOf(task.color_id!!))
            cardView.strokeWidth = 4
            cardView.setCardBackgroundColor(Color.parseColor(TaskColor.hexaBackgroundColorOf(task.color_id!!)))

            val buttonOptions = findViewById<MaterialButton>(R.id.opt_button)
            buttonOptions.setOnClickListener {
                openOptions(this, task, it)
            }
        }
        return itemView
    }

    private fun openOptions(taskItemView: View, task: Task, view: View) {
        val context = taskItemView.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.task_menu_options_layout)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.task_opt_edit -> this.openTaskForEdition(context, task)
                R.id.task_opt_finalize -> this.finalizeTaskOption(context, task)
            }
            true
        }
        popupMenu.show()
        true
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
        if(project.projectRole == KBProjectRole.MANAGER) {
            return false
        }
        return true
    }

    override fun isItemLocked(column_position: Int): Boolean {
        if(project.projectRole == KBProjectRole.VIEWER) {
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
            EditTaskDialogFragment(reloadFunction).show(context.supportFragmentManager, "edit_dialog")
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

    private fun openTaskForEdition(context: Context, task: Task) {
        if (context is BoardActivity) {
            val taskViewModel = ViewModelProviders.of(context).get(EditTaskViewModel::class.java)
            taskViewModel.dataTask.value = task
            EditTaskDialogFragment(reloadFunction).show(context.supportFragmentManager, "edit_dialog")
        }
    }

    private fun finalizeTaskOption(context: Context, task: Task) {
        AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.task_message_finalize))
            .setNeutralButton("Ok") { dialog, _ ->
                finalizeTask(context, task)
                dialog.dismiss()
            }
            .setNegativeButton("cancelar") {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun finalizeTask(context: Context, task: Task) {
        TaskAdapter.FinalizeTaskAsyncTask(context, task, reloadFunction).execute()
    }

}