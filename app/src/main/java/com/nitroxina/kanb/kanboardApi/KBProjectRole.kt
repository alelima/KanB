package com.nitroxina.kanb.kanboardApi

import java.io.Serializable

enum class KBProjectRole(val value: String) : Serializable{
    MANAGER("project-manager"),
    MEMBER("project-member"),
    VIEWER("project-viewer");

    companion object {
        fun getKBRoleByValue(value: String) : KBProjectRole? {
            for (kbRole in KBProjectRole.values()) {
                if (kbRole.value == value) {
                    return kbRole
                }
            }
            return null
        }
    }
}