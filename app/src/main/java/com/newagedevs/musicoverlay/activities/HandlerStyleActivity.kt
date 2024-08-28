package com.newagedevs.musicoverlay.activities

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslSeekBar
import com.newagedevs.musicoverlay.databinding.ActivityHandlerStyleBinding
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.services.OverlayService
import com.newagedevs.musicoverlay.view.ColorPaletteView
import com.newagedevs.musicoverlay.view.HandlerView
import dev.oneuiproject.oneui.widget.Toast


class HandlerStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener, HandlerView.HandlerPositionChangeListener {

    private lateinit var binding: ActivityHandlerStyleBinding
    lateinit var handlerView: HandlerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHandlerStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handlerView = HandlerView(this)

        binding.toolbarLayout.setNavigationButtonAsBack()

        // Handler properties
        val handlerIsLockPosition = SharedPrefRepository(this).isLockHandlerPositionEnabled()
        val handlerIsVibrateOnTouch = SharedPrefRepository(this).isVibrateHandlerOnTouchEnabled()
        val handlerPosition = SharedPrefRepository(this).getHandlerPosition()
        val handlerColor = SharedPrefRepository(this).getHandlerColor()
        val handlerTransparency = SharedPrefRepository(this).getHandlerTransparency()
        val handlerSize = SharedPrefRepository(this).getHandlerSize()
        val handlerWidth = SharedPrefRepository(this).getHandlerWidth()
        val translationY = SharedPrefRepository(this).getHandlerTranslationY()

        // Handler color
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
            handlerView.setVibrateOnClick(isChecked)
        }

        binding.rootLayout.addView(handlerView)

        // Set click listener for HandlerView
        handlerView.setOnClickListener {
            Toast.makeText(this, "Handler clicked", Toast.LENGTH_SHORT).show()
        }

        handlerView.setHandlerPositionIsLocked(handlerIsLockPosition)
        handlerView.setTranslationYPosition(translationY)
        handlerView.setViewGravity(if (handlerPosition == "Left") Gravity.START else Gravity.END)
        handlerView.setViewColor(handlerColor, handlerTransparency)
        handlerView.setViewDimension(Constants.handlerWidthList[handlerWidth], ((handlerSize * 1.5) + 200).toInt())
        handlerView.setHandlerPositionChangeListener(this)
        handlerView.setVibrateOnClick(handlerIsVibrateOnTouch)

    }

    override fun onColorSelected(color: Int) {
        val progress = SharedPrefRepository(this@HandlerStyleActivity).getHandlerTransparency()
        SharedPrefRepository(this).setHandlerColor(color)
        handlerView.setViewColor(color, progress)

    }

    private val transparencySeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                val progressValue = (progress * 2.55).toInt()
                val color = SharedPrefRepository(this@HandlerStyleActivity).getHandlerColor()
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerTransparency(255 - progressValue)
                handlerView.setViewColor(color, 255 - progressValue)
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

    private val sizeSeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerSize(progress)
                handlerView.setViewSize(((progress * 1.5) + 200).toInt())
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

    private val widthSeekBarChangeListener: SeslSeekBar.OnSeekBarChangeListener =
        object : SeslSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
                SharedPrefRepository(this@HandlerStyleActivity).setHandlerWidth(progress)
                handlerView.setViewWidth(Constants.handlerWidthList[progress])
            }
            override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }
        }

    override fun onVertical(rawY: Float) {
        SharedPrefRepository(this@HandlerStyleActivity).setHandlerTranslationY(rawY)
    }

    override fun onVertical(rawY: Int) { }

}