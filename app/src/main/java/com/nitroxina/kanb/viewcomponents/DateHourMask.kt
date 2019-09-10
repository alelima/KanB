package com.nitroxina.kanb.viewcomponents

import android.text.Editable
import android.text.TextWatcher
import java.util.*

class DateHourMask : TextWatcher {
    private val MAX_LENGTH = 12
    private val MIN_LENGTH = 2

    private var updatedText: String? = null
    private var editing: Boolean = false

    override fun afterTextChanged(editable: Editable?) {
        if (editing) return

        editing = true

        editable?.clear()
        editable?.insert(0, updatedText)

        editing = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        if (text.toString() == updatedText || editing) return

        var digits = text.toString().replace("\\D".toRegex(), "")
        val length = digits.length

        if (length <= MIN_LENGTH) {
            updatedText = digits
            return
        }

        if (length > MAX_LENGTH) {
            digits = digits.substring(0, MAX_LENGTH)
        }

        if (length <= 4) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2)

            updatedText = String.format(Locale.US, "%s/%s", month, day)
        } else if(length <=8) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2, 4)
            val year = digits.substring(4)

            updatedText = String.format(Locale.US, "%s/%s/%s", month, day, year)
        } else if(length <=10) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2, 4)
            val year = digits.substring(4, 8)
            val hour = digits.substring(8)
            updatedText = String.format(Locale.US, "%s/%s/%s %s:", month, day, year, hour)
        } else if(length <=12) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2, 4)
            val year = digits.substring(4, 8)
            val hour = digits.substring(8, 10)
            val minute = digits.substring(10)
            updatedText = String.format(Locale.US, "%s/%s/%s %s:%s", month, day, year, hour, minute)
        }
    }
}