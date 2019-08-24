package com.nitroxina.kanb.model

data class Column(
    val id: String,
    val title: String,
    val description: String?,
    val nb_tasks: Int?,
    val position: String?,
    val project_id: String?,
    val task_limit: String?
)