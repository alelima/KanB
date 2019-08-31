package com.nitroxina.kanb

import android.animation.ValueAnimator
import com.google.android.material.card.MaterialCardView


fun MaterialCardView.scaleView(height : Int) {
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
