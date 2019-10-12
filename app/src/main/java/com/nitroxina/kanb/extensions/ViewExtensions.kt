package com.nitroxina.kanb.extensions

import android.animation.ValueAnimator
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*


fun MaterialCardView.scaleHeight(height: Int) {
    val anim = ValueAnimator.ofInt(
        this.measuredHeightAndState,
        height
    )
    anim.addUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
        val layoutParams = this.layoutParams
        layoutParams.height = value
        this.layoutParams = layoutParams
    }
    anim.start()
}

fun String.isNumber(): Boolean {
    try {
        val number = this.toLong()
    } catch(nfe: NumberFormatException) {
        return false
    }
    return true
}

fun String.numberToBoolean() : Boolean {
    if(this == "1") {
        return true
    }
    return false
}

fun String.generateInitials() : String {
    var initials = ""
    val parts = this.split(" ")
    initials = if(parts.size >= 2) {
        parts[0].substring(0..0).toUpperCase() + parts[1].substring(0..0).toUpperCase()
    } else {
        this.substring(0..1).toUpperCase()
    }
    return initials
}

fun Date.timePassedSince(date: String): String {
    val formatter =  SimpleDateFormat("dd/MM/yyyy HH:mm")
    val dateCreation = formatter.parse(date)
    return ((this.time - dateCreation.time)/(1000*60*60*24)).toString()
}
