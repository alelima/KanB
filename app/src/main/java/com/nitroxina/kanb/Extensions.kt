package com.nitroxina.kanb

import com.nitroxina.kanb.model.*
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getKBResponse() : KBResponse {
    var id = this.getInt("id")
    var jsonrpc = this.getString("jsonrpc")
    var result = this.getString("result")

    return KBResponse(id, jsonrpc, result)
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
    val nbTasks = this.getInt("nb_tasks")

    return Column(id, title, description, nbTasks, position, projectId, taksLimit)
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
        columnList.add(jsonObject.toColumn())
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
    val categoryId = this.optString("category_id")
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
    val ownerId = this.optString("owner_id")
    val position = this.optString("position")
    val priority = this.optString("priority")
    val recurrenceBasedate = this.optString("recurrence_basedate")
    val recurrenceChild = this.optString("recurrence_child")
    val recurrenceFactor = this.optString("recurrence_factor")
    val recurrenceParent = this.optString("recurrence_parent")
    val recurrenceStatus = this.optString("recurrence_status")
    val recurrenceTimeFrame = this.optString("recurrence_timeframe")
    val recurrenceTrigger = this.optString("recurrence_trigger")
    val reference = this.optString("reference")
    val score = this.optString("score")
    val swimlaneId = this.optString("swimlane_id")
    val timeEstimated = this.optString("time_estimated")

    return Task(id, title, dateCreation, dateDue, projectId, categoryId, colorId, timeSpent, projectName, url,
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


