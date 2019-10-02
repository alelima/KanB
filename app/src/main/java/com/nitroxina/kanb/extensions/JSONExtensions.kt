package com.nitroxina.kanb.extensions

import com.nitroxina.kanb.kanboardApi.KBApplicationRole
import com.nitroxina.kanb.model.*
import com.nitroxina.kanb.online.KBError
import com.nitroxina.kanb.online.KBResponse
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getKBResponse() : KBResponse {
    var id = this.optInt("id")
    var jsonrpc = this.getString("jsonrpc")
    var result = this.optionString("result")
    var kbError: KBError? = null
    if(result == null || result.isEmpty()) {
        var error = ""
        error = this.optionString("error")
        error?.let {

            var jsonObject = JSONObject(it)
            kbError = KBError(jsonObject.optionString("code"), jsonObject.optionString("message"))
        }
    }

    return KBResponse(id, jsonrpc, result, kbError)
}

fun JSONObject.toUrl() : Url {
    val board = this.optionString("board")
    val calendar = this.optionString("calendar")
    val list = this.optionString("list")

    return Url(board, calendar, list)
}

fun JSONObject.toColumn() : Column {
    val id = this.optionString("id")
    val title = this.optionString("title")
    val position = this.optionString("position")
    val projectId = this.optionString("project_id")
    val taksLimit = this.optionString("task_limit")
    val description = this.optionString("description")
    val nbTasks = this.optInt("nb_tasks")
    val columnNbScore = this.optInt("column_nb_score")
    val columnNbTasks = this.optInt("column_nb_tasks")
    val columnScore = this.optInt("column_score")
    val hideInDashboard= this.optionString("hide_in_dashboard")
    val score = this.optInt("score")
    val tasks = this.optionString("tasks")

    val column = Column(id, title, description, nbTasks, position, projectId, taksLimit, columnNbScore, columnNbTasks,
        columnScore, hideInDashboard, score)

    if(tasks != null) {
        val tasksArray = JSONArray(tasks)
        for(i in 1..tasksArray.length()){
            val jsonObject = tasksArray[i-1] as JSONObject
            val task = jsonObject.toTask()
            column.tasks.add(task)
        }
    }

    return column
}

fun JSONObject.toProject() : Project {
    val name = this.getString("name")
    val id = this.getString("id")
    val isActive = this.optionString("is_active")
    val token= this.optionString("token")
    val lastModified= this.optionString("last_modified")
    val isPublic= this.optionString("is_public")
    val isPrivate= this.optionString("is_private")
    val defaultSwimlane= this.optionString("default_swimlane")
    val showDefaultSwimlane= this.getInt("show_default_swimlane")
    val description= this.optionString("description")
    val identifier= this.optionString("identifier")
    val columns = this.optionString("columns")
    val columnList = mutableListOf<Column>()

    if(columns.isNotEmpty()) {
        val jsonArrayColumn = JSONArray(columns)
        for (i in 1..jsonArrayColumn.length()) {
            val jsonObject = jsonArrayColumn[i - 1] as JSONObject
            var column = jsonObject.toColumn()
            column.project_name = name
            columnList.add(column)
        }
    }

    val urlJson = this.optionString("url")

    val url = if(urlJson.isNotEmpty()) { JSONObject(urlJson).toUrl() } else { null }

    return Project(id, name, defaultSwimlane, description, isActive, identifier,
        isPrivate, isPublic, token, lastModified,
        showDefaultSwimlane, columnList, url)
}

fun JSONObject.toTask() : Task {
    val id = this.getString("id")
    val title = this.getString("title")
    val dateCreation = this.optionString("date_creation")
    val dateDue = this.optionString("date_due")
    val projectId = this.optionString("project_id")
    val categoryId = this.optInt("category_id")
    val colorId = this.optionString("color_id")
    val timeSpent = this.optionString("time_spent")
    val projectName = this.optionString("project_name")
    val url = this.optionString("url")// optionString("url")
    val columnId = this.optionString("column_id")
    val columnName = this.optionString("column_name")
    val creatorId = this.optionString("creator_id")
    val dateCompleted = this.optionString("date_completed")
    val dateModification = this.optionString("date_modification")
    val dateMoved = this.optionString("date_moved")
    val dateStarted = this.optionString("date_started")
    val description = this.optionString("description")
    val externalProvider = this.optionString("external_provider")
    val externalUri = this.optionString("external_uri")
    val isActive = this.optionString("is_active")
    val ownerId = this.optInt("owner_id")
    val position = this.optionString("position")
    val priority = this.optInt("priority")
    val recurrenceBasedate = this.optionString("recurrence_basedate")
    val recurrenceChild = this.optionString("recurrence_child")
    val recurrenceFactor = this.optionString("recurrence_factor")
    val recurrenceParent = this.optionString("recurrence_parent")
    val recurrenceStatus = this.optionString("recurrence_status")
    val recurrenceTimeFrame = this.optionString("recurrence_timeframe")
    val recurrenceTrigger = this.optionString("recurrence_trigger")
    val reference = this.optionString("reference")
    val score = this.optInt("score")
    val swimlaneId = this.optionString("swimlane_id")
    val swimlaneName = this.optionString("swimlane_name")
    val timeEstimated = this.optionString("time_estimated")
    val assigneeName = this.optionString("assignee_name")

    return Task(title, projectId, id, dateCreation, dateDue, categoryId, colorId, timeSpent, projectName, url,
        columnId, columnName, creatorId, dateCompleted, dateModification, dateMoved, dateStarted, description,
        externalProvider, externalUri, isActive, ownerId, position, priority, recurrenceBasedate, recurrenceChild,
        recurrenceFactor, recurrenceParent, recurrenceStatus, recurrenceTimeFrame, recurrenceTrigger, reference,
        score, swimlaneId, swimlaneName, timeEstimated, assigneeName)
}

fun JSONObject.toProfile() : Profile {
    val id = this.getString("id")
    val username = this.getString("username")
    val email = this.optionString("email")
    val name = this.optionString("name")
    val apiAccessToken = this.optionString("api_access_token")
    val avatarPath = this.optionString("avatar_path")
    val disableLoginForm = this.optionString("disable_login_form")
    val filter = this.optionString("filter")
    val githubId = this.optionString("github_id")
    val gitlabId = this.optionString("gitlab_id")
    val googleId = this.optionString("google_id")
    val isActive = this.optionString("is_active").numberToBoolean()
    val isLdapUser = this.optBoolean("is_ldap_user")
    val language = this.optionString("language")
    val lockExpirationDate = this.optionString("lock_expiration_date")
    val nbFailedLogin = this.optionString("nb_failed_login")
    val notificationsEnabled = this.optBoolean("notifications_enabled")
    val notificationsFilter = this.optionString("notifications_filter")
    val role = KBApplicationRole.getKBAplicationRole(this.optionString("role"))
    val timezone = this.optionString("timezone")
    val token = this.optionString("token")
    val twofactorActivated = this.optBoolean("twofactor_activated")
    val twofactorSecret = this.optionString("twofactor_secret")

    return Profile(id, username, email, name, apiAccessToken, avatarPath, disableLoginForm,
        filter, githubId, gitlabId, googleId, isActive, isLdapUser, language, lockExpirationDate,
        nbFailedLogin, notificationsEnabled, notificationsFilter, role, timezone,
        token, twofactorActivated, twofactorSecret)
}

fun JSONArray.toBoard() : Board {
    val board = Board()
    for(i in 1..this.length()){
        val jsonObject = this[i-1] as JSONObject
        val swimlane = jsonObject.toSwimlane()
        board.swimlanes.add(swimlane)
    }
    return board
}

fun JSONObject.toSwimlane(): Swimlane {
    val id = this.getString("id")
    val name = this.getString("name")
    val description = this.optionString("description")
    val is_active = this.optionString("is_active")
    val nb_columns = this.optInt("nb_columns")
    val nb_swimlanes = this.optInt("nb_swimlanes")
    val nb_tasks = this.optInt("nb_tasks")
    val position = this.optionString("position")
    val project_id = this.optionString("project_id")
    val score = this.optInt("score")

    val swimlane = Swimlane(id, name, description, is_active, nb_columns, nb_swimlanes,
        nb_tasks, position, project_id, score)

    val columns = this.optionString("columns")
    if (columns != null) {
        val columnsArray = JSONArray(columns)
        for(i in 1..columnsArray.length()){
            val jsonObject = columnsArray[i-1] as JSONObject
            val column = jsonObject.toColumn()
            swimlane.columns.add(column)
        }
    }
    return swimlane
}

fun JSONObject.toCategory(): Category {
    val id = this.getString("id")
    val name = this.getString("name")
    val projectId = this.optionString("project_id")

    return Category(id, name, projectId)
}

fun JSONObject.optionString(name: String): String {
    val value = this.optString(name)
    return if (value == "null") "" else value 
}




