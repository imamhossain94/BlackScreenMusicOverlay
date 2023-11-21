package com.newagedevs.musicoverlay.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockStyle
import com.newagedevs.musicoverlay.models.ClockType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TextClockView : LinearLayout {
    private var mHourTextView: TextView? = null
    private var mMinuteTextView: TextView? = null
    private var mMeridianTextView: TextView? = null
    private var mTextClockAutoUpdate: Boolean = false
    private var mHourColor: Int = Color.WHITE
    private var mHourTextSize: Float = 26f
    private var mMinuteColor: Int = Color.RED
    private var mMinuteTextSize: Float = 26f
    private var mMeridianColor: Int = Color.GRAY
    private var mMeridianTextSize: Float = 26f
    private var mClockType: ClockType = ClockType.HOUR_12
    private var mClockStyle: ClockStyle = ClockStyle.VERTICAL_NUMBER
    private var mTimePattern: String = "HH:mm"

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Retrieve attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextClockView)
        mTextClockAutoUpdate = typedArray.getBoolean(R.styleable.TextClockView_textClockAutoUpdate, false)
        mHourColor = typedArray.getColor(R.styleable.TextClockView_hourColor, Color.WHITE)
        mHourTextSize = typedArray.getDimension(R.styleable.TextClockView_hourTextSize, 26f)
        mMinuteColor = typedArray.getColor(R.styleable.TextClockView_minuteColor, ContextCompat.getColor(context, R.color.lightRed))
        mMinuteTextSize = typedArray.getDimension(R.styleable.TextClockView_minuteTextSize, 26f)
        mMeridianColor = typedArray.getColor(R.styleable.TextClockView_meridianColor, Color.GRAY)
        mMeridianTextSize = typedArray.getDimension(R.styleable.TextClockView_meridianTextSize, 26f)
        val clockTypeValue = typedArray.getInt(R.styleable.TextClockView_clockType, 0)
        mClockType = if (clockTypeValue == 1) ClockType.HOUR_24 else ClockType.HOUR_12
        val clockStyleValue = typedArray.getInt(R.styleable.TextClockView_clockStyle, 0)
        mClockStyle = when (clockStyleValue) {
            1 -> ClockStyle.HORIZONTAL_NUMBER
            2 -> ClockStyle.HORIZONTAL_NUMBER_2
            3 -> ClockStyle.VERTICAL_TEXT
            else -> ClockStyle.VERTICAL_NUMBER
        }
        mTimePattern = typedArray.getString(R.styleable.TextClockView_timePattern) ?: "HH:mm"
        typedArray.recycle()

        // Create and add TextViews for hours, minutes, and meridian
        mHourTextView = createTextView(context, mHourColor, mHourTextSize)
        mMinuteTextView = createTextView(context, mMinuteColor, mMinuteTextSize)
        mMeridianTextView = createTextView(context, mMeridianColor, mMeridianTextSize)

        // Add TextViews to the layout
        addView(mHourTextView)
        addView(mMinuteTextView)
        if(mTimePattern.containsMeridian()) {
            addView(mMeridianTextView)
        }

        // Set initial values
        updateTime()

        gravity = if (mClockStyle == ClockStyle.VERTICAL_TEXT) Gravity.CENTER_VERTICAL or Gravity.START else Gravity.CENTER

        // Listen for time changes and update TextViews accordingly
        if (mTextClockAutoUpdate) {
            val timeTicker = object : Runnable {
                override fun run() {
                    updateTime()
                    postDelayed(this, 1000) // Update every second
                }
            }
            post(timeTicker)
        }
    }

    private fun createTextView(context: Context, textColor: Int, textSize: Float): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = when {
                mClockStyle == ClockStyle.HORIZONTAL_NUMBER_2 && this@TextClockView.indexOfChild(this) == 2 -> Gravity.START
                else -> Gravity.CENTER
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            this.setTextColor(textColor)
        }
    }

    private fun updateTime() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(mTimePattern, Locale.getDefault())
        val formattedTime = dateFormat.format(calendar.time)

        try {
            when (mClockStyle) {
                ClockStyle.VERTICAL_NUMBER -> {
                    orientation = VERTICAL
                    mHourTextView?.text = String.format("%02d", if (calendar[Calendar.HOUR] == 0) 12 else getHourValue(formattedTime))
                    mMinuteTextView?.text = String.format("%02d", calendar[Calendar.MINUTE])
                    if(mTimePattern.containsMeridian()) {
                        mMeridianTextView?.text = getMeridian(formattedTime)
                    }
                }
                ClockStyle.HORIZONTAL_NUMBER -> {
                    orientation = HORIZONTAL
                    mHourTextView?.text = String.format("%02d", if (calendar[Calendar.HOUR] == 0) 12 else getHourValue(formattedTime))
                    mMinuteTextView?.text = String.format(" %02d", calendar[Calendar.MINUTE])
                    if(mTimePattern.containsMeridian()) {
                        mMeridianTextView?.text = getMeridian(formattedTime)
                    }
                }
                ClockStyle.HORIZONTAL_NUMBER_2 -> {
                    orientation = HORIZONTAL
                    mHourTextView?.text = String.format("%02d", if (calendar[Calendar.HOUR] == 0) 12 else getHourValue(formattedTime))
                    mMinuteTextView?.text = String.format("\n%02d", calendar[Calendar.MINUTE])
                    if(mTimePattern.containsMeridian()) {
                        mMeridianTextView?.text = getMeridian(formattedTime)
                    }
                }
                ClockStyle.VERTICAL_TEXT -> {
                    orientation = VERTICAL
                    mHourTextView?.text = convertToWords(getHourValue(formattedTime))
                    mMinuteTextView?.text = convertToWords(calendar[Calendar.MINUTE])
                    if(mTimePattern.containsMeridian()) {
                        mMeridianTextView?.text = getMeridian(formattedTime)
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun getHourValue(formattedTime: String): Int {
        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
        return if (mClockType == ClockType.HOUR_12) {
            val hour = hourFormat.format(hourFormat.parse(formattedTime)!!)
            val hourValue = Integer.parseInt(hour)
            if (hourValue == 0) 12 else hourValue
        } else {
            val hour24Format = SimpleDateFormat("H", Locale.getDefault())
            val hour24 = hour24Format.format(hour24Format.parse(formattedTime)!!)
            Integer.parseInt(hour24)
        }
    }

    private fun String.containsMeridian(): Boolean {
        return this.contains("a")
    }

    private fun getMeridian(formattedTime: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(formattedTime)

            val meridianFormat = SimpleDateFormat("a", Locale.getDefault())
            meridianFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun convertToWords(number: Int): String {
        val units = arrayOf("Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        return when {
            number < 20 -> units[number]
            number < 100 -> tens[number / 10] + if (number % 10 != 0) " " + units[number % 10] else ""
            else -> throw IllegalArgumentException("Number must be less than 100")
        }
    }

    fun setClockStyle(style: ClockStyle) {
        mClockStyle = style
        updateTime()
    }

    fun setClockType(type: ClockType) {
        mClockType = type
        updateTime()
    }

    // Function to set hour view properties
    fun setHourTextColor(color: Int) {
        mHourTextView?.setTextColor(color)
    }

    fun setHourTextSize(size: Float) {
        mHourTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    // Function to set minute view properties
    fun setMinuteTextColor(color: Int) {
        mMinuteTextView?.setTextColor(color)
    }

    fun setMinuteTextSize(size: Float) {
        mMinuteTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    // Function to set meridian view properties
    fun setMeridianTextColor(color: Int) {
        mMeridianTextView?.setTextColor(color)
    }

    fun setMeridianTextSize(size: Float) {
        mMeridianTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    // Foreground view properties
    fun setForegroundColor(color: Int) {
        mHourTextView?.setTextColor(color)
        mMinuteTextView?.setTextColor(color)
        mMeridianTextView?.setTextColor(color)
    }

    fun setOpacity(value: Float) {
        mHourTextView?.alpha = value
        mMinuteTextView?.alpha = value
        mMeridianTextView?.alpha = value
    }



}

