package com.nitroxina.kanb.kanboardApi

import java.io.Serializable

enum class KBApplicationRole(val value: String) : Serializable {
    ADMINISTRATOR("app-admin"),
    MANAGER("app-manager"),
    USER("app-user");

    companion object {
        fun getKBAplicationRole(value: String) : KBApplicationRole? {
            for (kbApplicationRole in values()) {
                if (kbApplicationRole.value == value) {
                    return kbApplicationRole
                }
            }
            return null
        }
    }
}