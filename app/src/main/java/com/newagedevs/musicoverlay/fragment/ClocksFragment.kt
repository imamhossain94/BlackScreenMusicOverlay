package com.newagedevs.musicoverlay.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.newagedevs.musicoverlay.adapter.ClockAdapter
import com.newagedevs.musicoverlay.databinding.FragmentClocksBinding
import com.newagedevs.musicoverlay.models.ClockModel
import com.newagedevs.musicoverlay.models.ClockViewType

class ClocksFragment : Fragment() {

    private var _binding: FragmentClocksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access views using binding

        val clockList = generateClockList()
        val adapter = ClockAdapter(clockList)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerView.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun generateClockList(): List<ClockModel> {
        val clockList = mutableListOf<ClockModel>()

        // Add TextClocks to the list
        for (i in 0 until 3) {
            clockList.add(ClockModel(ClockViewType.TEXT_CLOCK.ordinal))
        }

        // Add FrameClocks to the list
        for (i in 0 until 3) {
            clockList.add(ClockModel(ClockViewType.FRAME_CLOCK.ordinal))
        }

        return clockList
    }

}
