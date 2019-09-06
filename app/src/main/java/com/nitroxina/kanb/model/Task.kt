package com.nitroxina.kanb.model

import org.json.JSONObject

data class Task(
    val id: String,
    var title: String,
    val date_creation: String?,
    var date_due: String?,
    val project_id: String,
    var category_id: Int?,
    var color_id: String,
    val time_spent: String?,
    val project_name: String,
    val url: String?,
    val column_id: String?,
    val creator_id: String?,
    val date_completed: String?,
    val date_modification: String?,
    val date_moved: String?,
    val date_started: String?,
    val description: String?,
    val external_provider: String?,
    val external_uri: String?,
    val is_active: String?,
    val owner_id: Int?,
    val position: String?,
    val priority: Int?,
    val recurrence_basedate: String?,
    val recurrence_child: String?,
    val recurrence_factor: String?,
    val recurrence_parent: String?,
    val recurrence_status: String?,
    val recurrence_timeframe: String?,
    val recurrence_trigger: String?,
    val reference: String?,
    val score: Int?,
    val swimlane_id: String?,
    val time_estimated: String?,
    val color: Color? = null
) {
    val subTasks: MutableList<SubTask> = mutableListOf<SubTask>()

    fun toJsonParameters(): String? {
        var json = "{\"id\": ${this.id}, \"title\": \"${this.title}\""

        if (!color_id.isNullOrEmpty()) {
            json += ", \"color_id\": \"$color_id\""
        }

        if (owner_id != null) {
            json += ", \"owner_id\": $owner_id"
        }

//        if (!(this.date_due).isNullOrEmpty()) {
//            json += ", \"date_due\": \"$date_due\" "
//        }

        if (!(this.description).isNullOrEmpty()) {
            json += ", \"description\": ${JSONObject.quote(description)}"
        }

        if (category_id != null ) {
            json += ", \"category_id\": $category_id"
        }

        if (score != null) {
            json += ", \"score\": $score"
        }

        if (priority != null) {
            json += ", \"priority\": $priority"
        }

        if (!reference.isNullOrEmpty()) {
            json += ", \"reference\": \"$reference\""
        }

//        if (!date_started.isNullOrEmpty()) {
//            json += ", \"date_started\": \"$date_started\" "
//        }

        //TODO: tags will be in future version
//        if (tags != null) {
//            json += "\"reference\": \"$reference\""
//        }

        json += "}"
        return json
    }

}