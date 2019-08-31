package com.nitroxina.kanb.kanboardApi

import java.io.Serializable

enum class KBRole(val value: String) : Serializable{
    MANAGER("project-manager"),
    MEMBER("project-member"),
    VIEWER("project-viewer");

    companion object {
        fun getKBRoleByValue(value: String) : KBRole? {
            for (kbRole in KBRole.values()) {
                if (kbRole.value == value) {
                    return kbRole
                }
            }
            return null
        }
    }
}