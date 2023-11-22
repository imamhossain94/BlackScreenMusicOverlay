package com.newagedevs.musicoverlay.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SeslSeekBar
import androidx.fragment.app.Fragment
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
        activityBinding.textClockPreview.setForegroundColor(color)
        activityBinding.frameClockPreview.setForegroundColor(color)
    }

    override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.textClockPreview.setOpacity((105f - progress.toFloat()) / 100.0f)
        activityBinding.frameClockPreview.setOpacity(300 - (progress * 2.55).toInt())
    }

    override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }

}
