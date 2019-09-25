package com.nitroxina.kanb.model

import java.io.Serializable

class Swimlane (
    val id: String,
    val name: String,
    val description: String,
    val is_active: String,
    val nb_columns: Int,
    val nb_swimlanes: Int,
    val nb_tasks: Int,
    val position: String,
    val project_id: String,
    val score: Int,
    val columns: MutableList<Column> = mutableListOf()
) : Serializable