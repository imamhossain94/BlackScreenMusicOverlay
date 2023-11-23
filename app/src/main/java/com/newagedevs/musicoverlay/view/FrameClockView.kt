package com.newagedevs.musicoverlay.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockModel
import com.newagedevs.musicoverlay.models.ClockStyle
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class FrameClockView : View {
    private var mBackgroundPaint: Paint? = null
    private var mHandPaint: Paint? = null

    private var frameClockAutoUpdate: Boolean = false

    private var showFrame: Boolean = false
    private var frameRadius: Float = 40f
    private var frameColor: Int = Color.GRAY
    private var frameThickness: Float = 2f

    private var showSecondsHand: Boolean = false
    private var secondHandColor: Int = Color.GRAY
    private var secondHandThickness: Float = 3f

    private var hourHandColor: Int = Color.WHITE
    private var hourHandThickness: Float = 5f

    private var minuteHandColor: Int = Color.RED
    private var minuteHandThickness: Float = 5f

    private var paintAlpha: Int = 255

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
        mHandPaint!!.isAntiAlias = true


        // Load custom attributes from XML layout
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.FrameClockView)
            try {
                frameClockAutoUpdate = typedArray.getBoolean(R.styleable.FrameClockView_frameClockAutoUpdate, false)

                showFrame = typedArray.getBoolean(R.styleable.FrameClockView_showFrame, false)
                frameRadius = typedArray.getDimension(R.styleable.FrameClockView_frameRadius, 40f)
                frameColor = typedArray.getColor(R.styleable.FrameClockView_frameColor, Color.GRAY)
                frameThickness = typedArray.getDimension(R.styleable.FrameClockView_frameThickness, 2f)

                showSecondsHand = typedArray.getBoolean(R.styleable.FrameClockView_showSecondsHand, false)
                secondHandColor = typedArray.getColor(R.styleable.FrameClockView_secondHandColor, Color.GRAY)
                secondHandThickness = typedArray.getDimension(R.styleable.FrameClockView_secondHandThickness, 3f)

                hourHandColor = typedArray.getColor(R.styleable.FrameClockView_hourHandColor, Color.WHITE)
                hourHandThickness = typedArray.getDimension(R.styleable.FrameClockView_hourHandThickness, 5f)

                minuteHandColor = typedArray.getColor(R.styleable.FrameClockView_minuteHandColor, Color.RED)
                minuteHandThickness = typedArray.getDimension(R.styleable.FrameClockView_minuteHandThickness, 5f)
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // always keep square size 1:1
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val measuredSize = width.coerceAtMost(height)
        setMeasuredDimension(measuredSize, measuredSize)
    }

    /**
     * Runnable instantiated only once
     */
    private val invalidator = Runnable { invalidate() }

    override fun onDraw(canvas: Canvas) {
        if (showFrame) {
            drawRectClockFrame(canvas)
        }
        drawBackground(canvas)
        drawMinuteHand(canvas)
        drawHourHand(canvas)
        if (showSecondsHand) {
            drawSecondHand(canvas)
        }
        drawNail(canvas)
        // redraw itself in REDRAW_RATE millis
        if (frameClockAutoUpdate) {
            postDelayed(invalidator, REDRAW_RATE)
        }
    }

    private fun drawRectClockFrame(
        canvas: Canvas
    ) {
        val rectF = RectF()
        val padding = (frameThickness.takeIf { it > 0 }?.div(2f) ?: 16).toFloat()
        val cornerRadius = frameRadius.takeIf { it > 0 } ?: 20

        val framePaint = Paint().apply {
            color = frameColor
            alpha = paintAlpha
            style = Paint.Style.STROKE
            strokeWidth = frameThickness.takeIf { it > 0 } ?: 5f
        }

        rectF.set(padding, padding, (width - padding), (height - padding))
        canvas.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), framePaint)
    }

    private fun drawBackground(canvas: Canvas) {
        val bgrCircleRadius = height / 2f
        canvas.drawCircle(bgrCircleRadius, bgrCircleRadius, bgrCircleRadius, mBackgroundPaint!!)
    }

    private fun drawHourHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.2f
        val thickness = hourHandThickness.takeIf { it > 0 } ?: (width * 0.06f)
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        mHandPaint!!.color = hourHandColor
        mHandPaint!!.alpha = paintAlpha
        val angle = hoursAngle
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.3f
        val thickness = minuteHandThickness.takeIf { it > 0 } ?: (width * 0.05f)
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        val angle = minutesAngle
        mHandPaint!!.color = minuteHandColor
        mHandPaint!!.alpha = paintAlpha
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val viewRadius = width / 2f
        val handRadius = width * 0.4f
        val thickness = secondHandThickness.takeIf { it > 0 } ?: (width * 0.005f)
        mHandPaint!!.strokeWidth = thickness
        // coordinates of hand's end
        val angle = secondsAngle
        mHandPaint!!.color = secondHandColor
        mHandPaint!!.alpha = paintAlpha
        val x = getStopX(viewRadius, handRadius, angle)
        val y = getStopY(viewRadius, handRadius, angle)
        canvas.drawLine(viewRadius, viewRadius, x, y, mHandPaint!!)
    }

    private fun drawNail(canvas: Canvas) {
        val viewRadius = height / 2f
        val nailRadius = height * 0.02f
        mHandPaint!!.color = Color.BLACK
        mHandPaint!!.alpha = paintAlpha
        canvas.drawCircle(viewRadius, viewRadius, nailRadius, mHandPaint!!)
    }

    private fun getStopX(viewRadius: Float, handRadius: Float, angle: Double): Float {
        return (viewRadius + handRadius * sin(angle)).toFloat()
    }

    private fun getStopY(viewRadius: Float, handRadius: Float, angle: Double): Float {
        return (viewRadius - handRadius * cos(angle)).toFloat()
    }

    private val hoursAngle: Double
        get() {
            val c = Calendar.getInstance()
            val hours = c[Calendar.HOUR]
            val minutes = c[Calendar.MINUTE]
            val minutesFromStart = (hours * 60 + minutes)
            return 2 * Math.PI * minutesFromStart / 720
        }

    private val minutesAngle: Double
        get() {
            val c = Calendar.getInstance()
            val secondsFromStart = (c[Calendar.MINUTE] * 60 + c[Calendar.SECOND])
            return 2 * Math.PI * secondsFromStart / 3600
        }

    private val secondsAngle: Double
        get() {
            val c = Calendar.getInstance()
            val millisFromStart = c[Calendar.SECOND] * 1000 + c[Calendar.MILLISECOND]
            return 2 * Math.PI * millisFromStart / 60000
        }

    companion object {
        private const val REDRAW_RATE: Long = 20 // 20ms
    }

    fun setAutoUpdate(value: Boolean = true) {
        frameClockAutoUpdate = value
        invalidate()
    }
    fun showFrame(value: Boolean = true) {
        showFrame = value
        invalidate()
    }

    fun showSecondsHand(value: Boolean = true) {
        showSecondsHand = value
        invalidate()
    }

    // Foreground view properties
    fun setForegroundColor(color: Int) {
        frameColor = color
        secondHandColor = color
        hourHandColor = color
        minuteHandColor = color
        invalidate()
    }

    fun setOpacity(value: Int) {
        paintAlpha = value
        invalidate()
    }

    fun setAttributes(attr: ClockModel) {
        setAutoUpdate(attr.autoUpdate)
        showFrame(attr.showFrame)
        showSecondsHand(attr.showSecondsHand)

    }

}

