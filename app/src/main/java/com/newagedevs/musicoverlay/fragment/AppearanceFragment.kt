package com.newagedevs.musicoverlay.fragment

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.SeslSeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.activities.ClockStyleActivity
import com.newagedevs.musicoverlay.databinding.FragmentAppearanceBinding
import com.newagedevs.musicoverlay.view.ColorPaletteView


class AppearanceFragment : Fragment(), ColorPaletteView.ColorSelectionListener, SeslSeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentAppearanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppearanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access views using binding
        binding.colorPaletteView.setColorSelectionListener(this)
        binding.transparencySeekBar.setOnSeekBarChangeListener(this)
        binding.transparencySeekBar.max = 100
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onColorSelected(color: Int) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.clockPreview.setForegroundColor(color)
    }

    override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.clockPreview.setOpacity((105f - progress.toFloat()) / 100.0f)
    }

    override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }


}
