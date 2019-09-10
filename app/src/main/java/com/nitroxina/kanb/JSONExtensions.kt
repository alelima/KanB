package com.nitroxina.kanb

import com.nitroxina.kanb.model.*
import com.nitroxina.kanb.online.KBError
import com.nitroxina.kanb.online.KBResponse
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getKBResponse() : KBResponse {
    var id = this.optInt("id")
    var jsonrpc = this.getString("jsonrpc")
    var result = this.optString("result")
    var kbError: KBError? = null
    if(result == null || result.isEmpty()) {
        var error = ""
        error = this.optString("error")
        error?.let {

            var jsonObject = JSONObject(it)
            kbError = KBError(jsonObject.optString("code"), jsonObject.optString("message"))
        }
    }

    return KBResponse(id, jsonrpc, result, kbError)
}

fun JSONObject.toUrl() : Url {
    val board = this.optString("board")
    val calendar = this.optString("calendar")
    val list = this.optString("list")

    return Url(board, calendar, list)
}

fun JSONObject.toColumn() : Column {
    val id = this.optString("id")
    val title = this.optString("title")
    val position = this.optString("position")
    val projectId = this.optString("project_id")
    val taksLimit = this.optString("task_limit")
    val description = this.optString("description")
    val nbTasks = this.optInt("nb_tasks")
    val columnNbScore = this.optInt("column_nb_score")
    val columnNbTasks = this.optInt("column_nb_tasks")
    val columnScore = this.optInt("column_score")
    val hideInDashboard= this.optString("hide_in_dashboard")
    val score = this.optInt("score")
    val tasks = this.optString("tasks")

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
    val isActive = this.optString("is_active")
    val token= this.optString("token")
    val lastModified= this.optString("last_modified")
    val isPublic= this.optString("is_public")
    val isPrivate= this.optString("is_private")
    val defaultSwimlane= this.optString("default_swimlane")
    val showDefaultSwimlane= this.getInt("show_default_swimlane")
    val description= this.optString("description")
    val identifier= this.optString("identifier")
    val columns = this.optString("columns")
    val columnList = mutableListOf<Column>()
    val jsonArrayColumn = JSONArray(columns)
    for(i in 1..jsonArrayColumn.length()){
        val jsonObject = jsonArrayColumn[i-1] as JSONObject
        var column = jsonObject.toColumn()
        column.project_name  = name
        columnList.add(column)
    }
    val urlJson = this.optString("url")
    val url = JSONObject(urlJson).toUrl()

    return Project(id, name, defaultSwimlane, description, isActive, identifier,
        isPrivate, isPublic, token, lastModified,
        showDefaultSwimlane, columnList, url)
}

fun JSONObject.toTask() : Task {
    val id = this.getString("id")
    val title = this.getString("title")
    val dateCreation = this.optString("date_creation")
    val dateDue = this.optString("date_due")
    val projectId = this.optString("project_id")
    val categoryId = this.optInt("category_id")
    val colorId = this.optString("color_id")
    val timeSpent = this.optString("time_spent")
    val projectName = this.optString("project_name")
    val url = this.optString("url")// optString("url")
    val columnId = this.optString("column_id")
    val creatorId = this.optString("creator_id")
    val dateCompleted = this.optString("date_completed")
    val dateModification = this.optString("date_modification")
    val dateMoved = this.optString("date_moved")
    val dateStarted = this.optString("date_started")
    val description = this.optString("description")
    val externalProvider = this.optString("external_provider")
    val externalUri = this.optString("external_uri")
    val isActive = this.optString("is_active")
    val ownerId = this.optInt("owner_id")
    val position = this.optString("position")
    val priority = this.optInt("priority")
    val recurrenceBasedate = this.optString("recurrence_basedate")
    val recurrenceChild = this.optString("recurrence_child")
    val recurrenceFactor = this.optString("recurrence_factor")
    val recurrenceParent = this.optString("recurrence_parent")
    val recurrenceStatus = this.optString("recurrence_status")
    val recurrenceTimeFrame = this.optString("recurrence_timeframe")
    val recurrenceTrigger = this.optString("recurrence_trigger")
    val reference = this.optString("reference")
    val score = this.optInt("score")
    val swimlaneId = this.optString("swimlane_id")
    val timeEstimated = this.optString("time_estimated")

    return Task(title, projectId, id, dateCreation, dateDue, categoryId, colorId, timeSpent, projectName, url,
        columnId, creatorId, dateCompleted, dateModification, dateMoved, dateStarted, description,
        externalProvider, externalUri, isActive, ownerId, position, priority, recurrenceBasedate, recurrenceChild,
        recurrenceFactor, recurrenceParent, recurrenceStatus, recurrenceTimeFrame, recurrenceTrigger, reference,
        score, swimlaneId, timeEstimated)
}

fun JSONObject.toProfile() : Profile {
    val id = this.getString("id")
    val username = this.getString("username")
    val email = this.optString("email")
    val name = this.optString("name")
    val apiAccessToken = this.optString("api_access_token")
    val avatarPath = this.optString("avatar_path")
    val disableLoginForm = this.optString("disable_login_form")
    val filter = this.optString("filter")
    val githubId = this.optString("github_id")
    val gitlabId = this.optString("gitlab_id")
    val googleId = this.optString("google_id")
    val isActive = this.optString("is_active")
    val isLdapUser = this.optBoolean("is_ldap_user")
    val language = this.optString("language")
    val lockExpirationDate = this.optString("lock_expiration_date")
    val nbFailedLogin = this.optString("nb_failed_login")
    val notificationsEnabled = this.optString("notifications_enabled")
    val notificationsFilter = this.optString("notifications_filter")
    val role = this.optString("role")
    val timezone = this.optString("timezone")
    val token = this.optString("token")
    val twofactorActivated = this.optBoolean("twofactor_activated")
    val twofactorSecret = this.optString("twofactor_secret")

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
    val description = this.optString("description")
    val is_active = this.optString("is_active")
    val nb_columns = this.optInt("nb_columns")
    val nb_swimlanes = this.optInt("nb_swimlanes")
    val nb_tasks = this.optInt("nb_tasks")
    val position = this.optString("position")
    val project_id = this.optString("project_id")
    val score = this.optInt("score")

    val swimlane = Swimlane(id, name, description, is_active, nb_columns, nb_swimlanes,
        nb_tasks, position, project_id, score)

    val columns = this.optString("columns")
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
    val projectId = this.optString("project_id")

    return Category(id, name, projectId)
}





