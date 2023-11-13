package com.newagedevs.musicoverlay.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.R
import java.util.Calendar

class FrameClockView : View {
    private var mBackgroundPaint: Paint? = null
    private var mHandPaint: Paint? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = Color.TRANSPARENT
        mBackgroundPaint!!.isAntiAlias = true
        mHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHandPaint!!.strokeCap = Paint.Cap.ROUND
        mHandPaint!!.color = Color.WHITE
        mHandPaint!!.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // always keep square size 1:1
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val measuredSize = Math.min(width, height)
        setMeasuredDimension(measuredSize, measuredSize)
    }

    /**
     * Runnable instantiated only once
     */
    private val invalidator = Runnable { invalidate() }
    override fun onDraw(canvas: Canvas) {
        drawRectClockFrame(canvas)
        drawBackground(canvas)
        drawMinuteHand(canvas)
        drawHourHand(canvas)
        drawSecondHand(canvas);
        drawNail(canvas)
        // redraw itself in REDRAW_RATE millis
        postDelayed(invalidator, REDRAW_RATE)
    }

    private fun drawClockFrame(canvas: Canvas) {
        val padding = 15
        val viewRadius = width / 2f - padding
        val frameRadius = viewRadius * 1.1f // Adjust the frame size as needed
        val frameThickness = viewRadius * 0.01f // Adjust the frame thickness as needed
        val framePaint = Paint()
        framePaint.color = Color.WHITE
        framePaint.style = Paint.Style.STROKE
        framePaint.strokeWidth = frameThickness
        canvas.drawCircle(viewRadius + padding, viewRadius + padding, frameRadius, framePaint)
    }

    private fun drawRectClockFrame(canvas: Canvas) {
        val padding = 15
        val viewRadius = width / 2f - padding
        val frameSize = viewRadius * 1.1f // Adjust the frame size as needed
        val frameThickness = viewRadius * 0.01f // Adjust the frame thickness as needed
        val framePaint = Paint()
        framePaint.color = Color.WHITE
        framePaint.style = Paint.Style.STROKE
        framePaint.strokeWidth = frameThickness

        // Calculate the coordinates of the rounded square
        val left = (viewRadius + padding) - frameSize
        val top = (viewRadius + padding) - frameSize
        val right = (viewRadius + padding) + frameSize
        val bottom = (viewRadius + padding) + frameSize
        val cornerRadius = frameSize * 0.1f // Adjust the corner radius as needed

        // Draw the rounded square
        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, framePaint)
    }

    private fun drawBackground(canvas: Canvas) {
        val bgrCircleRadius = height / 2f
        canvas.drawCircle(bgrCircleRadius, bgrCircleRadius, bgrCircleRadius, mBackgroundPaint!!)
    }

    private fun drawHourHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.2f
        val thickness = width * 0.06f // 1% of view's width
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        mHandPaint!!.color = Color.WHITE
        val angle = hoursAngle
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.3f
        val thickness = width * 0.05f // 1% of view's width
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        val angle = minutesAngle
        mHandPaint!!.color = ContextCompat.getColor(context, R.color.lightRed)
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.4f
        val thickness = width * 0.005f // 0.5% of view's width
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        val angle = secondsAngle
        mHandPaint!!.color = ContextCompat.getColor(context, R.color.limeBlue)
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    /**
     * Evaluates hand's end X coordinate based on the given angle.
     * x = R + r * sin(a);
     */
    private fun getStopX(viewRadius: Float, handRadius: Float, angle: Double): Float {
        return (viewRadius + handRadius * Math.sin(angle)).toFloat()
    }

    /**
     * Evaluates hand's end Y coordinate based on the given angle
     * y = R - r * cos(a);
     */
    private fun getStopY(viewRadius: Float, handRadius: Float, angle: Double): Float {
        return (viewRadius - handRadius * Math.cos(angle)).toFloat()
    }

    private fun drawNail(canvas: Canvas) {
        val viewRadius = height / 2f
        val nailRadius = height * 0.02f
        mHandPaint!!.color = Color.BLACK
        canvas.drawCircle(viewRadius, viewRadius, nailRadius, mHandPaint!!)
    }

    private val hoursAngle: Double
        /**
         * Gets angle of hour hand(minute-accurate)
         * @return angle in radians
         */
        get() {
            val c = Calendar.getInstance()
            val hours = c[Calendar.HOUR]
            val minutes = c[Calendar.MINUTE]
            val minutesFromStart = (hours * 60
                    + minutes)
            return 2 * Math.PI * minutesFromStart / 720 // divided by number of minutes in 12 hours;
        }
    private val minutesAngle: Double
        /**
         * Gets angle of minute hand (second-accurate)
         * @return angle in radians
         */
        get() {
            val c = Calendar.getInstance()
            val secondsFromStart = (c[Calendar.MINUTE] * 60
                    + c[Calendar.SECOND])
            return 2 * Math.PI * secondsFromStart / 3600 // divided by number of seconds in 1 hour
        }
    private val secondsAngle: Double
        /**
         * Gets angle of second hand (millisecond-accurate)
         * @return angle in radians
         */
        get() {
            val c = Calendar.getInstance()
            val millisFromStart = c[Calendar.SECOND] * 1000 + c[Calendar.MILLISECOND]
            return 2 * Math.PI * millisFromStart / 60000 // divided by number of milliseconds in 1 minute
        }

    companion object {
        private val TAG = FrameClockView::class.java.simpleName
        private const val REDRAW_RATE: Long = 20 // 20ms
        private const val BACKGROUND_COLOR = -0xf677ae
    }
}
