package com.nitroxina.kanb.model

data class Board(
    val swimlanes: MutableList<Swimlane> = mutableListOf()
)