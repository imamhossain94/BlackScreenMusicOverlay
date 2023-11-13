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
import java.util.*

class TextClockView : LinearLayout {
    private var mHourTextView: TextView? = null
    private var mMinuteTextView: TextView? = null
    private var mClockStyle: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Retrieve attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextClockView)
        val textSize = typedArray.getDimension(R.styleable.TextClockView_textSize, 26f)
        mClockStyle = typedArray.getInt(R.styleable.TextClockView_clockStyle, 0)
        typedArray.recycle()

        // Create and add TextViews for hours and minutes
        mHourTextView = createTextView(context, Color.WHITE, textSize)
        mMinuteTextView = createTextView(context, ContextCompat.getColor(context, R.color.lightRed), textSize)

        // Add TextViews to the layout
        addView(mHourTextView)
        addView(mMinuteTextView)

        // Set initial values
        updateTime()

        gravity = if (mClockStyle == 2) Gravity.CENTER_VERTICAL or Gravity.START else Gravity.CENTER

        // Listen for time changes and update TextViews accordingly
        val timeTicker = object : Runnable {
            override fun run() {
                updateTime()
                postDelayed(this, 1000) // Update every second
            }
        }
        post(timeTicker)
    }

    private fun createTextView(context: Context, textColor: Int, textSize: Float): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = if (mClockStyle == 2) Gravity.START else Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            this.setTextColor(textColor)
        }
    }

    private fun updateTime() {
        val calendar = Calendar.getInstance()
        when (mClockStyle) {
            0 -> {
                // Vertical Number Clock
                orientation = VERTICAL
                mHourTextView?.text = String.format("%02d", calendar[Calendar.HOUR])
                mMinuteTextView?.text = String.format("%02d", calendar[Calendar.MINUTE])
            }
            1 -> {
                // Horizontal Number Clock
                orientation = HORIZONTAL
                mHourTextView?.text = String.format("%02d:", calendar[Calendar.HOUR])
                mMinuteTextView?.text = String.format("%02d", calendar[Calendar.MINUTE])
            }
            2 -> {
                // Vertical Text Clock
                orientation = VERTICAL
                mHourTextView?.text = convertToWords(calendar[Calendar.HOUR])
                mMinuteTextView?.text = convertToWords(calendar[Calendar.MINUTE])
            }
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

}
