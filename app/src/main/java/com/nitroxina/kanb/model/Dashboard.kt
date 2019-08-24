package com.nitroxina.kanb.model

data class Dashboard(
    val projects: List<Project>,
    val subtasks: List<Any>,
    val tasks: List<Task>
)