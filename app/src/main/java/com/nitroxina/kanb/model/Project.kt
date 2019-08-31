package com.nitroxina.kanb.model

import com.nitroxina.kanb.kanboardApi.KBRole
import java.io.Serializable

data class Project(
    val id: String,
    val name: String,
    val default_swimlane: String? = null,
    val description: String? = null,
    val is_active: String? = null,
    val identifier: String? = null,
    val is_private: String? = null,
    val is_public: String? = null,
    val token: String? = null,
    val last_modified: String? = null,
    val show_default_swimlane: Int? = null,
    val columns: List<Column> = mutableListOf<Column>(),
    val url: Url? = null,
    val email: String? = null,
    val end_date: String? = null,
    val is_everybody_allowed: String? = null,
    val owner_id: String? = null,
    val predefined_email_subjects: Any? = null,
    val priority_default: String? = null,
    val priority_end: String? = null,
    val priority_start: String? = null,
    val start_date: String? = null,
    var role : KBRole? = null
) : Serializable 