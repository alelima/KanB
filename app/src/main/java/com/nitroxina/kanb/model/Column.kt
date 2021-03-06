package com.nitroxina.kanb.model

import java.io.Serializable

data class Column(
    val id: String,
    val title: String,
    val description: String?,
    val nb_tasks: Int?,
    val position: String?,
    val project_id: String?,
    val task_limit: String?,
    val column_nb_score: Int?,
    val column_nb_tasks: Int?,
    val column_score: Int?,
    val hide_in_dashboard: String?,
    val score: Int?,
    var project_name: String? = null,
    val tasks: MutableList<Task> = mutableListOf()
) : Serializable