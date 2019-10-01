package com.nitroxina.kanb.model

import com.nitroxina.kanb.kanboardApi.KBApplicationRole
import java.io.Serializable

data class Profile(
    val id: String,
    val username: String,
    val email: String,
    val name: String?,
    val api_access_token: String,
    val avatar_path: String?,
    val disable_login_form: String,
    val filter: String?,
    val github_id: String?,
    val gitlab_id: String?,
    val google_id: String?,
    val is_active: Boolean,
    val is_ldap_user: Boolean,
    val language: String?,
    val lock_expiration_date: String,
    val nb_failed_login: String,
    val notifications_enabled: Boolean,
    val notifications_filter: String,
    val role: KBApplicationRole?,
    val timezone: String?,
    val token: String,
    val twofactor_activated: Boolean,
    val twofactor_secret: String
) : Serializable