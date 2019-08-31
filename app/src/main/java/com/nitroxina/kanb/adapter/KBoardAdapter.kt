package com.nitroxina.kanb.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.allyants.boardview.SimpleBoardAdapter
import com.nitroxina.kanb.R
import com.nitroxina.kanb.kanboardApi.KBRole
import com.nitroxina.kanb.model.Column
import com.nitroxina.kanb.model.Task
import java.util.ArrayList

@Suppress("UNCHECKED_CAST")
class KBoardAdapter(context: Context?, data: ArrayList<KBColumn>, val role: KBRole?) : SimpleBoardAdapter(context, data as ArrayList<SimpleColumn>) {

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

    class KBColumn(column: com.nitroxina.kanb.model.Column, items: ArrayList<Any>) : Column() {
        init {
            this.header_object = column
            this.objects = items
        }
    }

}