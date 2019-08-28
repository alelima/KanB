package com.nitroxina.kanb.adapter

import android.content.Context
import android.view.View
import com.allyants.boardview.SimpleBoardAdapter
import java.util.ArrayList

@Suppress("UNCHECKED_CAST")
class KBoardAdapter(context: Context?, data: ArrayList<KBColumn>) : SimpleBoardAdapter(context, data as ArrayList<SimpleColumn>) {

    override fun maxItemCount(column_position: Int): Int {
        return -1 // no size restrition
    }

    override fun isColumnLocked(column_position: Int): Boolean {
        return true
    }

    override fun createFooterView(context: Context?, footer_object: Any?, column_position: Int): View? {
        return null
    }

    override fun createFooterObject(column_position: Int): Any? {
        return null
    }

    class KBColumn(columnId : String, title: String, items: ArrayList<Any>) : SimpleColumn(title, items)

}