package com.nitroxina.kanb.model

import java.io.Serializable

data class Url(
    val board: String?,
    val calendar: String?,
    val list: String?
) : Serializable