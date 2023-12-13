package com.newagedevs.musicoverlay.activities

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslSeekBar
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.databinding.ActivityHandlerStyleBinding
import com.newagedevs.musicoverlay.fragment.SettingsFragment
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.ColorPaletteView

class HandlerStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener {

    private lateinit var binding: ActivityHandlerStyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHandlerStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        val handlerColor = SharedPrefRepository(this).getHandlerColor()
        handlerColor.let { binding.colorPaletteView.setDefaultSelectedColor(it) }

        val alpha = SharedPrefRepository(this).getOverlayTransparency()
        binding.handlerTransparency.setProgressValue((alpha / 2.55).toInt())



        binding.vibrateOnHandleIsTouched.isChecked = SharedPrefRepository(this).isVibrateHandlerOnTouchEnabled()
        binding.vibrateOnHandleIsTouched.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefRepository(this).setVibrateHandlerOnTouchEnabled(isChecked)
        }
    }

    override fun onColorSelected(color: Int) {
        SharedPrefRepository(this).setHandlerColor(color)
    }


}