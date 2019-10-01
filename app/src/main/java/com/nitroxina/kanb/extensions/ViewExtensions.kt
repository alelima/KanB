package com.nitroxina.kanb.extensions

import android.animation.ValueAnimator
import com.google.android.material.card.MaterialCardView


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
