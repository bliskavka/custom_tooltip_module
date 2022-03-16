/*
Copyright 2016 Tomer Goldstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.tomergoldst.tooltips

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.animation.OvershootInterpolator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator

internal class DefaultTooltipAnimator : TooltipAnimator {

    override fun animateShow(view: View): ObjectAnimator {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        val show = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        )
        show.duration = DEFAULT_ANIM_DURATION
        show.interpolator = OvershootInterpolator()
        return show
    }

    override fun animateHide(
        view: View,
        animatorListenerAdapter: AnimatorListenerAdapter
    ): ObjectAnimator {
        val hide = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
            PropertyValuesHolder.ofFloat("scaleX", 1f, 0f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 0f)
        )
        hide.duration = DEFAULT_ANIM_DURATION
        hide.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
                animatorListenerAdapter.onAnimationEnd(animation)
            }
        })
        hide.interpolator = AnticipateOvershootInterpolator()
        return hide
    }

    companion object {
        private const val DEFAULT_ANIM_DURATION = 400L
    }
}