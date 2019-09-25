package com.nitroxina.kanb.model

import java.io.Serializable

data class Board(
    val swimlanes: MutableList<Swimlane> = mutableListOf()
) : Serializable {
    fun boardResume() : String {
        var boardResume = ""
        this.swimlanes[0].columns.forEach {
            boardResume += it.tasks.size.toString() + " " + it.title + "  "
        }
        return boardResume
    }
}