package com.nitroxina.kanb.model

import android.content.res.TypedArray
import android.graphics.Color

data class TaskColor(
    val background: String,
    val border: String,
    val name: String
) {
    companion object {
        private val colorBackgroundMap = hashMapOf<String, String>(
            "yellow" to "#f5f7c4",
            "blue" to "#dbebff",
            "green" to "#bdf4cb",
            "purple" to "#dfb0ff",
            "red" to "#ffbbbb",
            "orange" to "#ffd7b3",
            "grey" to "#eeeeee",
            "brown" to "#d7ccc8",
            "deep_orange" to "#ffab91",
            "dark_grey" to "#cfd8dc",
            "pink" to "#f48fb1",
            "teal" to "#80cbc4",
            "cyan" to "#b2ebf2",
            "lime" to "#e6ee9c",
            "light_green" to "#dcedc8",
            "amber" to "#ffe082"
        )

        private val colorBorderMap = hashMapOf<String, String>(
            "yellow" to "#dfe32d",
            "blue" to "#a8cfff",
            "green" to "#4ae371",
            "purple" to "#cd85fe",
            "red" to "#ff9797",
            "orange" to "#ffac62",
            "grey" to "#cccccc",
            "brown" to "#4e342e",
            "deep_orange" to "#e64a19",
            "dark_grey" to "#455a64",
            "pink" to "#d81b60",
            "teal" to "#00695c",
            "cyan" to "#00bcd4",
            "lime" to "#afb42b",
            "light_green" to "#689f38",
            "amber" to "#ffa000"
        )

        fun hexaBackgroundColorOf(nameColor: String) : String? {
            return colorBackgroundMap[nameColor]
        }

        fun hexaBorderColorOf(nameColor: String) : String? {
            return colorBorderMap[nameColor]
        }

        fun getRandomMaterialColor(colors: TypedArray): Int {
            val index = (Math.random() * colors.length()).toInt()
            val returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
            return returnColor
        }
    }
}