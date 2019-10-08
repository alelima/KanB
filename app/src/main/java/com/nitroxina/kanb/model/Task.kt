package com.nitroxina.kanb.model

import android.os.Bundle
import com.nitroxina.kanb.extensions.isNumber
import org.json.JSONObject
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Task(
    var title: String,
    val project_id: String,
    var id: String? = null,
    _date_creation: String? = null,
    _date_due: String? = null,
    var category_id: Int? = null,
    var color_id: String? = null,
    var time_spent: String? = null,
    var project_name: String? = null,
    val url: String? = null,
    var column_id: String? = null,
    var column_name: String? = null,
    val creator_id: String? = null,
    _date_completed: String? = null,
    _date_modification: String? = null,
    _date_moved: String? = null,
    _date_started: String? = null,
    var description: String? = null,
    val external_provider: String? = null,
    val external_uri: String? = null,
    val is_active: String? = null,
    var owner_id: Int? = null,
    var position: String? = null,
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
    var swimlane_id: String? = null,
    var swimlane_name: String? = null,
    var time_estimated: String? = null,
    var assignee_name: String? = null,
    val color: TaskColor? = null
) : Serializable {
    var category_name: String? = ""
    var creator_name: String = ""
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
    var date_modification: String? = null
        set(value) {
            if(!value.isNullOrBlank() && value.isNumber()) {
                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                field = formatter.format(Date((value + "000").toLong()))
            } else {
                field = value
            }
        }
    var date_moved: String? = null
        set(value) {
            if(!value.isNullOrBlank() && value.isNumber()) {
                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                field = formatter.format(Date((value + "000").toLong()))
            } else {
                field = value
            }
        }
    var date_completed: String? = null
        set(value) {
            if(!value.isNullOrBlank() && value.isNumber()) {
                val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
                field = formatter.format(Date((value + "000").toLong()))
            } else {
                field = value
            }
        }
    var date_creation: String? = null
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
        date_modification = _date_modification
        date_moved = _date_moved
        date_completed = _date_completed
        date_creation = _date_creation
    }

    fun toJsonUpdateParameters(): String {
        var json = "{\"id\": ${this.id}, \"title\": \"${this.title}\""
        json = addJsonParameters(json) + "}"
        return json
    }

    fun toJsonCreateParameters(): String {
        var json = "{\"title\": \"${this.title}\", \"project_id\":${this.project_id}"
        if (!column_id.isNullOrEmpty()) {
            json += ", \"column_id\": ${this.column_id}"
        }
        json = addJsonParameters(json) + "}"
        return json
    }

    fun toJsonMovePositionParameters(): String {
        var json = "{\"project_id\":${this.project_id},"
        json += "\"task_id\": ${this.id},"
        json += "\"column_id\": ${this.column_id},"
        json += "\"position\": ${this.position},"
        json += "\"swimlane_id\": ${this.swimlane_id} }"
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

        if (!time_spent.isNullOrEmpty()) {
            json1 += ", \"time_spent\": \"$time_spent\" "
        }

        if (!time_estimated.isNullOrEmpty()) {
            json1 += ", \"time_estimated\": \"$time_estimated\" "
        }

        //TODO: tags will be in future version
//        if (tags != null) {
//            json += "\"reference\": \"$reference\""
//        }
        return json1
    }

    fun toBundle() : Bundle {
        val bundle = Bundle()
        bundle.putSerializable("task", this)
        return bundle
    }
}

