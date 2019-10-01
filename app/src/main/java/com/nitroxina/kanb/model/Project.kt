package com.nitroxina.kanb.model

import com.nitroxina.kanb.kanboardApi.KBProjectRole
import org.json.JSONObject
import java.io.Serializable

data class Project(
    val id: String? = null,
    var name: String? = null,
    val default_swimlane: String? = null,
    var description: String? = null,
    val is_active: String? = null,
    var identifier: String? = null,
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
    var owner_id: String? = null,
    val predefined_email_subjects: Any? = null,
    val priority_default: String? = null,
    val priority_end: String? = null,
    val priority_start: String? = null,
    val start_date: String? = null,
    var projectRole : KBProjectRole? = null
) : Serializable {

    var board: Board? = null

    fun toJsonCreateParameters() : String {
        var json = "{ \"${this::name.name}\": \"${this.name}\""
        if (!(this.description).isNullOrEmpty()) {
            json += ", \"${this::description.name}\": ${JSONObject.quote(description)}"
        }
        if (!(this.identifier).isNullOrEmpty()) {
            json += ", \"${this::identifier.name}\": \"$identifier\""
        }

        if (!(this.owner_id).isNullOrEmpty()) {
            json += ", \"${this::owner_id.name}\": $owner_id }"
        }
        return json;
    }

    fun isPrivate() : Boolean {
        var result = false

        if(!this.is_private.isNullOrEmpty() && is_private == "1") {
            result = true
        }

        return result
    }
}