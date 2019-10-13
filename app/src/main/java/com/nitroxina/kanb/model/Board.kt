package com.nitroxina.kanb.model

import java.io.Serializable

data class Board(
    val swimlanes: MutableList<Swimlane> = mutableListOf()
) : Serializable {
    fun boardResume(showOnlyActives: Boolean) : String {
        var boardResume = ""
        this.swimlanes[0].columns.forEach {
            boardResume += if(showOnlyActives) {
                var count = 0
                it.tasks.forEach { task ->
                    if(!task.is_active.isNullOrEmpty() && task.is_active.toBoolean()) {
                        count += 1
                    }
                }
                count.toString() + " " + it.title + "  "
            } else {
                it.tasks.size.toString() + " " + it.title + "  "
            }
        }
        return boardResume
    }
}