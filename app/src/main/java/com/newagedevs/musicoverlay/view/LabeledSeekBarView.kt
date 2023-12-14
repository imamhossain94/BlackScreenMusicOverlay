package com.newagedevs.musicoverlay.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SeslSeekBar
import com.newagedevs.musicoverlay.R

class LabeledSeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleTextView: TextView
    private val lowValueTextView: TextView
    private val highValueTextView: TextView
    private val seekBar: SeslSeekBar

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.labeled_seekbar_view, this, true)

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        lowValueTextView = findViewById(R.id.lowValueTextView)
        highValueTextView = findViewById(R.id.highValueTextView)
        seekBar = findViewById(R.id.seekBar)

        // Retrieve custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabeledSeekBarView)
        setTitle(typedArray.getString(R.styleable.LabeledSeekBarView_title) ?: "Title")
        setLowText(typedArray.getString(R.styleable.LabeledSeekBarView_lowText) ?: "Low")
        setHighText(typedArray.getString(R.styleable.LabeledSeekBarView_highText) ?: "High")
        setProgressValue(typedArray.getInt(R.styleable.LabeledSeekBarView_progressValue, 0))
        typedArray.recycle()

        // Customize the views or perform additional initialization...
    }

    // Setter methods for custom attributes
    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setLowText(lowText: String) {
        lowValueTextView.text = lowText
    }

    fun setHighText(highText: String) {
        highValueTextView.text = highText
    }

    fun setProgressValue(progress: Int) {
        seekBar.progress = progress
    }

    // Other methods as needed...
    fun setOnSeekBarChangeListener(listener: SeslSeekBar.OnSeekBarChangeListener) {
        seekBar.setOnSeekBarChangeListener(listener)
    }

}
