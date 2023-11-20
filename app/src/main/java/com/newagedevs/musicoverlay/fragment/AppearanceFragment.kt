package com.newagedevs.musicoverlay.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.activities.ClockStyleActivity
import com.newagedevs.musicoverlay.databinding.FragmentAppearanceBinding
import com.newagedevs.musicoverlay.view.ColorPaletteView

class AppearanceFragment : Fragment(), ColorPaletteView.ColorSelectionListener {

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onColorSelected(color: Int) {
        val activityBinding = (requireActivity() as ClockStyleActivity).binding
        activityBinding.clockViewHolder.background = createDrawableWithColor(color)
    }

    // Function to create a GradientDrawable with a specific color
    private fun createDrawableWithColor(color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)

        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(
            R.attr.colorSecondaryText,
            typedValue,
            true
        )

        drawable.setStroke(2.dpToPx(), ContextCompat.getColor(requireContext(), typedValue.resourceId))
        drawable.cornerRadius = 16.dpToPx().toFloat()
        return drawable
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}
