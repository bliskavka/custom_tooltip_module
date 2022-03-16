package com.tomergoldst.tooltips

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.*

internal object UiUtils {

    @JvmStatic
    fun isRtl() = isRtl(Locale.getDefault())

    private fun isRtl(locale: Locale): Boolean {
        val directionality = Character.getDirectionality(locale.displayName[0]).toInt()
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
    }

    fun toPixel(context: Context, dp: Float): Float {
        val densityDpi = context.resources.displayMetrics.densityDpi.toFloat()
        return dp * (densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun toDp(context: Context, pixel: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, context.resources.displayMetrics);
    }
}