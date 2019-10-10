package com.nitroxina.kanb.model

import com.nitroxina.kanb.kanboardApi.KBApplicationRole

class User(
    val id: String,
    val email: String?,
    val is_active: Boolean,
    val name: String?,
    val role: KBApplicationRole?,
    val username: String
)