package com.tomergoldst.tooltips

import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.view.View

interface TooltipAnimator {
    /**
     * Object animator for the tooltip view to pop-up.
     * @param view The tooltip view.
     * @return ObjectAnimator
     */
    fun animateShow(view: View): ObjectAnimator

    /**
     * Object animator for the tooltip view to pop-out/hide.
     * @param view The tooltip view.
     * @param animatorListenerAdapter The animator listener adapter to listen for animation event.
     * @return ObjectAnimator
     */
    fun animateHide(view: View, animatorListenerAdapter: AnimatorListenerAdapter): ObjectAnimator
}