package com.newagedevs.musicoverlay.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.newagedevs.musicoverlay.R
import java.util.Calendar

class CustomClockView : RelativeLayout {
    private var minTextView: TextView? = null
    private var hrTextView: TextView? = null
    private var amPmTextView: TextView? = null
    private var secTextView: TextView? = null
    private var dividerView: View? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        // Create and customize the views
        createDividerView()
        createMinTextView()
        createHrTextView()
        createAmPmTextView()
        createSecTextView()

        // Add the views to the layout
        addView(dividerView)
        addView(hrTextView)
        addView(minTextView)
        addView(amPmTextView)
        addView(secTextView)

        // Set initial values
        updateTime()

        // Listen for time changes and update TextViews accordingly
        val timeTicker = object : Runnable {
            override fun run() {
                updateTime()
                postDelayed(this, 1000) // Update every second
            }
        }
        post(timeTicker)
    }

    private fun createDividerView() {
        dividerView = View(context)
        val params = RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            2.dpToPx()
        )
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        dividerView?.layoutParams = params
        dividerView?.setBackgroundColor(Color.WHITE)
    }

    private fun createMinTextView() {
        minTextView = createTextView("00", R.drawable.rounded_corners4)
        val params = RelativeLayout.LayoutParams(
            40.dpToPx(),
            40.dpToPx()
        )
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        minTextView?.layoutParams = params
    }

    private fun createHrTextView() {
        hrTextView = createTextView("00", R.drawable.rounded_corners3)
        val params = RelativeLayout.LayoutParams(
            40.dpToPx(),
            40.dpToPx()
        )
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        minTextView?.id?.let { params.addRule(RelativeLayout.LEFT_OF, it) }
        params.setMargins(0, 0, 4.dpToPx(), 0)
        hrTextView?.layoutParams = params
    }

    private fun createAmPmTextView() {
        amPmTextView = createTextView("aa", R.drawable.rounded_corners5)
        val params = RelativeLayout.LayoutParams(
            40.dpToPx(),
            40.dpToPx()
        )
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        minTextView?.id?.let { params.addRule(RelativeLayout.RIGHT_OF, it) }
        params.setMargins(4.dpToPx(), 0, 0, 0)
        amPmTextView?.layoutParams = params
    }

    private fun createSecTextView() {
        secTextView = createTextView("00", R.drawable.rounded_corners6)
        val params = RelativeLayout.LayoutParams(
            18.dpToPx(),
            18.dpToPx()
        )
        amPmTextView?.id?.let { params.addRule(RelativeLayout.BELOW, it) }
        minTextView?.id?.let { params.addRule(RelativeLayout.RIGHT_OF, it) }
        params.setMargins((-7).dpToPx(), (-15).dpToPx(), 0, 0)
        secTextView?.layoutParams = params
    }

    private fun createTextView(text: String, backgroundResource: Int): TextView {
        return TextView(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundResource(backgroundResource)
            this.text = text
        }
    }

    private fun updateTime() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm"

        hrTextView?.text = String.format("%02d", hour)
        minTextView?.text = String.format("%02d", minute)
        amPmTextView?.text = amPm
        secTextView?.text = String.format("%02d", second)

        // Schedule the next update after one second
        postDelayed({ updateTime() }, 1000)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
