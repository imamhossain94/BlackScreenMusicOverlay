package com.newagedevs.musicoverlay.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.newagedevs.musicoverlay.activities.ClockStyleActivity
import com.newagedevs.musicoverlay.adapter.ClockAdapter
import com.newagedevs.musicoverlay.databinding.FragmentClocksBinding
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository

class ClocksFragment : Fragment(), ClockAdapter.OnClockItemClickListener {

    private var _binding: FragmentClocksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access views using binding
        val clockIndex = SharedPrefRepository(requireActivity()).getClockStyleIndex()
        val adapter = ClockAdapter(Constants.clockList, this, selectedItemPosition = clockIndex)

        binding.recyclerView.layoutManager = context?.let { NonScrollableGridLayoutManager(it, 3) }
        binding.recyclerView.adapter = adapter

        val clock = Constants.clockList[clockIndex]
        val activityBinding = (requireActivity() as ClockStyleActivity).binding

        when(clock.viewType) {
            ClockViewType.TEXT_CLOCK.ordinal -> {
                activityBinding.textClockPreview.visibility = View.VISIBLE
                activityBinding.frameClockPreview.visibility = View.GONE
                activityBinding.textClockPreview.setAttributes(clock)
            }
            ClockViewType.FRAME_CLOCK.ordinal -> {
                activityBinding.textClockPreview.visibility = View.GONE
                activityBinding.frameClockPreview.visibility = View.VISIBLE
                activityBinding.frameClockPreview.setAttributes(clock)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTextClockClick(position: Int) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.textClockPreview.visibility = View.VISIBLE
        activityBinding.frameClockPreview.visibility = View.GONE
        activityBinding.textClockPreview.setAttributes(Constants.clockList[position])
        SharedPrefRepository(requireActivity()).setClockStyleIndex(position)
    }

    override fun onFrameClockClick(position: Int) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.textClockPreview.visibility = View.GONE
        activityBinding.frameClockPreview.visibility = View.VISIBLE
        activityBinding.frameClockPreview.setAttributes(Constants.clockList[position])
        SharedPrefRepository(requireActivity()).setClockStyleIndex(position)
    }

    inner class NonScrollableGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
        override fun canScrollHorizontally(): Boolean {
            return false
        }
        override fun canScrollVertically(): Boolean {
            return false
        }
    }
}
