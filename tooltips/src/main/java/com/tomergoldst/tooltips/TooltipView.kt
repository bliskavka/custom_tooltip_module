package com.tomergoldst.tooltips

import android.content.Context
import android.graphics.*
import android.widget.FrameLayout
import androidx.core.view.children
import com.tomergoldst.tooltips.TooltipView.ArrowAlignment.*
import com.tomergoldst.tooltips.TooltipView.ArrowPosition.*

class TooltipView @JvmOverloads constructor(
    context: Context
) : FrameLayout(context) {

    var arrowAlignment: ArrowAlignment = CENTER
    var arrowPosition: ArrowPosition = BELOW
    var tooltipColor: Int = Color.BLACK
    var cornerRadius: Float = UiUtils.toPixel(context, CORNER_RADIUS)
    var arrowHeight: Int = UiUtils.toPixel(context, ARROW_HEIGHT).toInt()
    var arrowWidth: Int = UiUtils.toPixel(context, ARROW_WIDTH).toInt()
    var arrowOffset: Int = UiUtils.toPixel(context, ARROW_OFFSET).toInt()

    private var tooltipHeight: Int = 0
    private var tooltipWidth: Int = 0

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = tooltipColor
        isAntiAlias = true
    }

    var arrowTip = Point(0,0)
        private set

    fun setContentLayout(layoutId: Int) {
        inflate(context, layoutId, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        if (tooltipWidth == 0 || tooltipHeight == 0) {
            tooltipWidth = measuredWidth
            tooltipHeight = measuredHeight

            if (arrowPosition == RIGHT || arrowPosition == LEFT) {
                tooltipWidth += arrowHeight
            } else {
                tooltipHeight += arrowHeight
            }
        }
        setMeasuredDimension(tooltipWidth, tooltipHeight)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        val rectLeftTop: Point
        val rectRightBottom: Point

        // Define rectangle coordinates
        when (arrowPosition) {
            ABOVE -> {
                rectLeftTop = Point(0, 0 + arrowHeight)
                rectRightBottom = Point(width, height)
            }
            BELOW -> {
                rectLeftTop = Point(0, 0)
                rectRightBottom = Point(width, height - arrowHeight)
            }
            LEFT -> {
                rectLeftTop = Point(0 + arrowHeight, 0)
                rectRightBottom = Point(width, height)
            }
            RIGHT -> {
                rectLeftTop = Point(0, 0)
                rectRightBottom = Point(width - arrowHeight, height)
            }
        }

        // Set content layout margins
        (children.first().layoutParams as LayoutParams).also {
            it.setMargins(rectLeftTop.x, rectLeftTop.y, 0, 0)
            children.first().layoutParams = it
        }

        val rect = RectF(
            rectLeftTop.x.toFloat(),
            rectLeftTop.y.toFloat(),
            rectRightBottom.x.toFloat(),
            rectRightBottom.y.toFloat()
        )
        val arrowCoordinate = getArrowBaseCoordinate(rect, arrowPosition, arrowAlignment)
        val arrowPath = getArrowPath(arrowCoordinate.x, arrowCoordinate.y, arrowWidth, arrowHeight, arrowPosition)

        canvas?.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        canvas?.drawPath(arrowPath, paint)
        super.dispatchDraw(canvas)
    }

    private fun getArrowPath(x: Int, y: Int, width: Int, height: Int, position: ArrowPosition): Path {
        val p1: Point
        val p2: Point
        val p3: Point

        when (position) {
            ABOVE -> {
                p1 = Point(x - width / 2, y)
                p2 = Point(x, y - height)
                p3 = Point(x + width / 2, y)
            }
            BELOW -> {
                p1 = Point(x - width / 2, y)
                p2 = Point(x, y + height)
                p3 = Point(x + width / 2, y)
            }
            RIGHT -> {
                p1 = Point(x, y - width / 2)
                p2 = Point(x + height, y)
                p3 = Point(x, y + width / 2)
            }
            LEFT -> {
                p1 = Point(x, y + width / 2)
                p2 = Point(x - height, y)
                p3 = Point(x, y - width / 2)
            }
        }

        arrowTip = p2
        return Path().apply {
            fillType = Path.FillType.EVEN_ODD
            moveTo(p1.x.toFloat(), p1.y.toFloat())
            lineTo(p2.x.toFloat(), p2.y.toFloat())
            lineTo(p3.x.toFloat(), p3.y.toFloat())
            close()
        }
    }

    private fun getArrowBaseCoordinate(
        rect: RectF,
        position: ArrowPosition,
        alignment: ArrowAlignment
    ): Point {

        fun offset(value: Float) = when (alignment) {
            CENTER -> (value / 2).toInt()
            START -> arrowOffset
            END -> (value - arrowOffset).toInt()
        }

        return when (position) {
            ABOVE -> Point(rect.left.toInt() + offset(rect.width()), rect.top.toInt())
            BELOW -> Point(rect.left.toInt() + offset(rect.width()), rect.bottom.toInt())
            LEFT -> Point(rect.left.toInt(), rect.top.toInt() + offset(rect.height()))
            RIGHT -> Point(rect.right.toInt(), rect.top.toInt() + offset(rect.height()))
        }

    }

    companion object {
        const val CORNER_RADIUS = 4f
        const val ARROW_HEIGHT = 7f
        const val ARROW_WIDTH = 14f
        const val ARROW_OFFSET = 20f
    }

    enum class ArrowPosition {
        ABOVE,
        BELOW,
        RIGHT,
        LEFT
    }

    enum class ArrowAlignment {
        START,
        END,
        CENTER
    }
}