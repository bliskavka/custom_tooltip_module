package com.tomergoldst.tooltips

import android.animation.Animator
import android.widget.TextView
import android.os.Build
import android.view.ViewOutlineProvider
import android.annotation.SuppressLint
import android.graphics.Outline
import android.animation.AnimatorListenerAdapter
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import java.util.ArrayList
import java.util.HashMap

class ToolTipsManager(
    private val listener: TipListener? = null
) {
    private val mTipsMap: MutableMap<Int, View> = HashMap()
    private var mTooltipAnimator: TooltipAnimator = DefaultTooltipAnimator()

    interface TipListener {
        fun onTipDismissed(view: View?, anchorViewId: Int, byUser: Boolean)
    }

    fun show(toolTip: ToolTip): View? {
        val tipView = create(toolTip) ?: return null
        mTooltipAnimator.animateShow(tipView).start()
        return tipView
    }

    private fun create(toolTip: ToolTip): View? {
        if (mTipsMap.containsKey(toolTip.anchorView.id)) {
            return mTipsMap[toolTip.anchorView.id]
        }

        val tipView = TooltipView(toolTip.context).apply {
            setContentLayout(R.layout.tootip_simple)
            when (toolTip.position) {
                ToolTip.POSITION_ABOVE -> { arrowPosition = TooltipView.ArrowPosition.BELOW }
                ToolTip.POSITION_BELOW -> { arrowPosition = TooltipView.ArrowPosition.ABOVE }
                ToolTip.POSITION_LEFT_TO -> { arrowPosition = TooltipView.ArrowPosition.RIGHT }
                ToolTip.POSITION_RIGHT_TO -> { arrowPosition = TooltipView.ArrowPosition.LEFT }
            }
            when (toolTip.align) {
                ToolTip.ALIGN_CENTER -> { arrowAlignment = TooltipView.ArrowAlignment.CENTER }
                ToolTip.ALIGN_LEFT -> { arrowAlignment = TooltipView.ArrowAlignment.START }
                ToolTip.ALIGN_RIGHT -> { arrowAlignment = TooltipView.ArrowAlignment.END }
            }
        }

        toolTip.rootView.addView(tipView)
        moveTipToCorrectPosition(
            tipView = tipView,
            point = ToolTipCoordinatesFinder.getCoordinates(tipView, toolTip)
        )

        val btn = tipView.findViewById<Button>(R.id.button122)
        btn.setOnClickListener { dismiss(tipView, true) }

        toolTip.anchorView.id.let {
            tipView.tag = it
            mTipsMap[it] = tipView
        }
        return tipView
    }

    private fun moveTipToCorrectPosition(tipView: View, point: Point) {
        val tipViewCoordinates = Coordinates(tipView)
        val translationX = point.x - tipViewCoordinates.left
        val translationY = point.y - tipViewCoordinates.top
        tipView.translationX = if (!UiUtils.isRtl()) translationX.toFloat() else -translationX.toFloat()
        tipView.translationY = translationY.toFloat()
    }

    fun setToolTipAnimator(animator: TooltipAnimator) {
        mTooltipAnimator = animator
    }

    fun dismiss(tipView: View?, byUser: Boolean): Boolean {
        if (tipView != null && tipView.isVisible) {
            val key = tipView.tag as Int
            mTipsMap.remove(key)
            animateDismiss(tipView, byUser)
            return true
        }
        return false
    }

    fun findAndDismiss(anchorView: View): Boolean {
        val view = find(anchorView.id)
        return view != null && dismiss(view, false)
    }

    fun dismissAll() {
        if (mTipsMap.isNotEmpty()) {
            val entries: List<Map.Entry<Int, View>> =
                ArrayList<Map.Entry<Int, View>>(mTipsMap.entries)
            for ((_, value) in entries) {
                dismiss(value, false)
            }
        }
        mTipsMap.clear()
    }

    private fun find(key: Int): View? {
        return if (mTipsMap.containsKey(key)) {
            mTipsMap[key]
        } else null
    }

    private fun animateDismiss(view: View, byUser: Boolean) {
        mTooltipAnimator.animateHide(
            view,
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    listener?.onTipDismissed(view, view.tag as Int, byUser)
                    (view.parent as ViewGroup).removeView(view)
                }
            }).start()
    }
}