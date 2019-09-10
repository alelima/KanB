package com.nitroxina.kanb.model

import com.nitroxina.kanb.adapter.ItemDropdown

class Category (
    override val id: String, override val name: String, val project_id: String
) : ItemDropdown