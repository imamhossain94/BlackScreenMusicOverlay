package com.newagedevs.musicoverlay.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SeslSeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.activities.ClockStyleActivity
import com.newagedevs.musicoverlay.databinding.FragmentAppearanceBinding
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
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

        val clockColor = SharedPrefRepository(requireActivity()).getClockColor()
        clockColor?.let { binding.colorPaletteView.setDefaultSelectedColor(it) }

        val clockIndex = SharedPrefRepository(requireActivity()).getClockStyleIndex()
        val clock = Constants.clockList[clockIndex]


        when(clock.viewType) {
            ClockViewType.TEXT_CLOCK.ordinal -> {
                val textTransparency = SharedPrefRepository(requireActivity()).getTextClockTransparency()
                binding.transparencySeekBar.progress = ((105 - textTransparency * 100).toInt())
            }
            ClockViewType.FRAME_CLOCK.ordinal -> {
                val frameTransparency = SharedPrefRepository(requireActivity()).getFrameClockTransparency()
                binding.transparencySeekBar.progress = ((300 - frameTransparency) / 2.55).toInt()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onColorSelected(color: Int) {
        val activity = (requireActivity() as ClockStyleActivity)
        activity.binding.textClockPreview.setMinuteTextColor(color)
        activity.binding.frameClockPreview.setMinuteHandColor(color)

        val updatedClockList = Constants.clockList.map {
            it.copy(minuteColor = color, minuteHandColor = color)
        }.toList()

        activity.clocksFragment.adapter.updateClockList(updatedClockList)

        SharedPrefRepository(requireActivity()).setClockColor(color)
    }

    override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        val textTransparency = (105f - progress.toFloat()) / 100.0f
        val frameTransparency = 300 - (progress * 2.55).toInt()

        activityBinding.textClockPreview.setOpacity(textTransparency)
        activityBinding.frameClockPreview.setOpacity(frameTransparency)

        SharedPrefRepository(requireActivity()).setTextClockTransparency(textTransparency)
        SharedPrefRepository(requireActivity()).setFrameClockTransparency(frameTransparency)
    }

    override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }

}
