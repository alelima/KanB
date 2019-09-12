package com.nitroxina.kanb.model

import com.nitroxina.kanb.isNumber
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Task(
    var title: String,
    val project_id: String,
    var id: String? = null,
    val date_creation: String? = null,
    _date_due: String? = null,
    var category_id: Int? = null,
    var color_id: String? = null,
    var time_spent: String? = null,
    var project_name: String? = null,
    val url: String? = null,
    var column_id: String? = null,
    val creator_id: String? = null,
    val date_completed: String? = null,
    val date_modification: String? = null,
    val date_moved: String? = null,
    _date_started: String? = null,
    var description: String? = null,
    val external_provider: String? = null,
    val external_uri: String? = null,
    val is_active: String? = null,
    var owner_id: Int? = null,
    val position: String? = null,
    var priority: Int? = null,
    val recurrence_basedate: String? = null,
    val recurrence_child: String? = null,
    val recurrence_factor: String? = null,
    val recurrence_parent: String? = null,
    val recurrence_status: String? = null,
    val recurrence_timeframe: String? = null,
    val recurrence_trigger: String? = null,
    var reference: String? = null,
    var score: Int? = null,
    val swimlane_id: String? = null,
    var time_estimated: String? = null,
    val color: Color? = null
) {
    var category_name: String? = ""
    var owner_name: String? = ""
    val subTasks: MutableList<SubTask> = mutableListOf<SubTask>()
    var date_started: String? = null
        set(value) {
            if(!value.isNullOrBlank() && value.isNumber()) {
                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                field = formatter.format(Date((value + "000").toLong()))
            } else {
                field = value
            }
        }
    var date_due: String? = null
        set(value) {
            if(!value.isNullOrBlank() && value.isNumber()) {
                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                field = formatter.format(Date((value + "000").toLong()))
            } else {
                field = value
            }
        }

    init {
        date_started = _date_started
        date_due = _date_due
    }

    fun toJsonUpdateParameters(): String? {
        var json = "{\"id\": ${this.id}, \"title\": \"${this.title}\""
        json = addJsonParameters(json) + "}"
        return json
    }

    fun toJsonCreateParameters(): String? {
        var json = "{\"title\": \"${this.title}\", \"project_id\":${this.project_id}"

        if (!column_id.isNullOrEmpty()) {
            json += ", \"column_id\": ${this.column_id}"
        }
        json = addJsonParameters(json) + "}"
        return json
    }

    private fun addJsonParameters(json: String): String {
        var json1 = json
        if (!color_id.isNullOrEmpty()) {
            json1 += ", \"color_id\": \"$color_id\""
        }

        if (owner_id != null) {
            json1 += ", \"owner_id\": $owner_id"
        }

        if (!(this.date_due).isNullOrEmpty()) {
            json1 += ", \"date_due\": \"$date_due\" "
        }

        if (!(this.description).isNullOrEmpty()) {
            json1 += ", \"description\": ${JSONObject.quote(description)}"
        }

        if (category_id != null) {
            json1 += ", \"category_id\": $category_id"
        }

        if (score != null) {
            json1 += ", \"score\": $score"
        }

        if (priority != null) {
            json1 += ", \"priority\": $priority"
        }

        if (!reference.isNullOrEmpty()) {
            json1 += ", \"reference\": \"$reference\""
        }

        if (!date_started.isNullOrEmpty()) {
            json1 += ", \"date_started\": \"$date_started\" "
        }

        //TODO: tags will be in future version
//        if (tags != null) {
//            json += "\"reference\": \"$reference\""
//        }
        return json1
    }
}

