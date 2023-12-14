package com.newagedevs.musicoverlay.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SeslSeekBar
import com.newagedevs.musicoverlay.R
import dev.oneuiproject.oneui.utils.SeekBarUtils
import dev.oneuiproject.oneui.widget.HapticSeekBar

class LabeledHapticSeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleTextView: TextView
    private val lowValueTextView: TextView
    private val highValueTextView: TextView
    private val seekBar: HapticSeekBar

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.labeled_haptic_seekbar_view, this, true)

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        lowValueTextView = findViewById(R.id.lowValueTextView)
        highValueTextView = findViewById(R.id.highValueTextView)
        seekBar = findViewById(R.id.seekBar)
        SeekBarUtils.showTickMark(seekBar, true)

        // Retrieve custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabeledHapticSeekBarView)
        setTitle(typedArray.getString(R.styleable.LabeledHapticSeekBarView_hapticTitle) ?: "Title")
        setLowText(typedArray.getString(R.styleable.LabeledHapticSeekBarView_hapticLowText) ?: "Low")
        setHighText(typedArray.getString(R.styleable.LabeledHapticSeekBarView_hapticHighText) ?: "High")
        setStepValue(typedArray.getInt(R.styleable.LabeledHapticSeekBarView_hapticStepValue, 0))
        setMaxStep(typedArray.getInt(R.styleable.LabeledHapticSeekBarView_hapticMaxStep, 2))
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

    fun setStepValue(step: Int) {

        Log.d("Lal:-----", "$step ha ha ah")

        seekBar.progress = step
    }

    fun setMaxStep(max: Int) {
        seekBar.max = max
    }

    // Other methods as needed...
    fun setOnSeekBarChangeListener(listener: SeslSeekBar.OnSeekBarChangeListener) {
        seekBar.setOnSeekBarChangeListener(listener)
    }
}
