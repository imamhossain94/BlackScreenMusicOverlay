package com.newagedevs.musicoverlay.activities

import com.newagedevs.musicoverlay.view.HandlerView
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslSeekBar
import com.newagedevs.musicoverlay.databinding.ActivityHandlerStyleBinding
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.ColorPaletteView
import dev.oneuiproject.oneui.widget.Toast


class HandlerStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener {

    private lateinit var binding: ActivityHandlerStyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHandlerStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        // Handler color
        val handlerColor = SharedPrefRepository(this).getHandlerColor()
        handlerColor.let { binding.colorPaletteView.setDefaultSelectedColor(it) }
        binding.colorPaletteView.setColorSelectionListener(this)

        // Handler transparency
        val alpha = SharedPrefRepository(this).getHandlerTransparency()
        binding.handlerTransparency.setProgressValue((alpha / 2.55).toInt())
        binding.handlerTransparency.setOnSeekBarChangeListener(transparencySeekBarChangeListener)

        // Handler size
        val size = SharedPrefRepository(this).getHandlerSize()
        binding.handlerSize.setProgressValue(size)
        binding.handlerSize.setOnSeekBarChangeListener(sizeSeekBarChangeListener)

        // Handler width
        val width = SharedPrefRepository(this).getHandlerWidth()
        binding.handlerWidth.setStepValue(width)
        binding.handlerWidth.setOnSeekBarChangeListener(widthSeekBarChangeListener)

        // Vibration on touch
        binding.vibrateOnHandleIsTouched.isChecked = SharedPrefRepository(this).isVibrateHandlerOnTouchEnabled()
        binding.vibrateOnHandleIsTouched.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefRepository(this).setVibrateHandlerOnTouchEnabled(isChecked)
        }





        // Add HandlerView on top of other views
        val handlerView = HandlerView(this)
//        val layoutParams = FrameLayout.LayoutParams(
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            FrameLayout.LayoutParams.WRAP_CONTENT
//        )

        binding.rootLayout.addView(handlerView)

        // Set click listener for HandlerView
        handlerView.setOnClickListener {
            // Handle click event

            Toast.makeText(this, "Handler clicked", Toast.LENGTH_SHORT).show()
        }

        handlerView.setViewGravity(Gravity.START)

    }

    override fun onColorSelected(color: Int) {
        SharedPrefRepository(this).setHandlerColor(color)
    }

    private val transparencySeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerTransparency((progress * 2.55).toInt())
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

    private val sizeSeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerSize(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

    private val widthSeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerWidth(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

}