package com.newagedevs.musicoverlay.view

import android.R.attr
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs


class HandlerView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    companion object {
        private const val TOUCH_MOVE_FACTOR: Long = 20
        private const val TOUCH_TIME_FACTOR: Long = 300
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
    }

    private var lastY = 0f
    private var actionDownPoint = PointF(0f, 0f)
    private var touchDownTime = 0L
    private var lastClickTime = 0L

    init {
        updateViewProperties(50, 300, Gravity.TOP or Gravity.END, Color.BLUE, 20f, Color.GRAY, 1f)
    }

    private fun createViewDrawable(color: Int, cornerRadius: Float, strokeColor: Int, strokeWidth: Float): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius)
        shape.setColor(color)
        shape.setStroke(strokeWidth.toInt(), strokeColor)
        return shape
    }
dd .
    private fun updateViewProperties(width: Int, height: Int, gravity: Int, color: Int, cornerRadius: Float, strokeColor: Int, strokeWidth: Float) {
        val layoutParams = FrameLayout.LayoutParams(width, height)
        layoutParams.gravity = gravity
        this.layoutParams = layoutParams

        background = createViewDrawable(color, cornerRadius, strokeColor, strokeWidth)
    }

    fun setViewSize(width: Int, height: Int) {
        val layoutParams = this.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = width
        layoutParams.height = height
        requestLayout()
    }

    fun setViewGravity(gravity: Int) {
        val layoutParams = this.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = gravity
        requestLayout()
    }

    fun setViewColor(color: Int) {
        val gradientDrawable = background as GradientDrawable
        gradientDrawable.setColor(color)
    }

    fun setCornerRadius(cornerRadius: Float) {
        val gradientDrawable = background as GradientDrawable
        gradientDrawable.cornerRadii = floatArrayOf(
            cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius
        )
    }

    fun setStrokeColor(strokeColor: Int) {
        (background.mutate() as GradientDrawable).setStroke(attr.width, strokeColor)
    }

    fun setStrokeWidth(strokeWidth: Int) {
        (background.mutate() as GradientDrawable).setStroke(strokeWidth, attr.color)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.rawY - lastY
                translationY += deltaY
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                val isTouchDuration = System.currentTimeMillis() - touchDownTime < TOUCH_TIME_FACTOR
                val isTouchLength = abs(event.x - actionDownPoint.x) + abs(event.y - actionDownPoint.y) < TOUCH_MOVE_FACTOR
                val shouldClick = isTouchLength && isTouchDuration

                if (shouldClick) {
                    if (System.currentTimeMillis() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        // Double click

                        lastClickTime = 0
                    } else {
                        lastClickTime = System.currentTimeMillis()
                        performClick()
                    }
                }
            }
        }
        return true
    }
}

