package com.newagedevs.musicoverlay

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.newagedevs.musicoverlay.databinding.ActivityClockStyleBinding
import com.newagedevs.musicoverlay.extension.OnSwipeTouchListener
import com.newagedevs.musicoverlay.extension.ResizeAnimation

class ClockStyleActivity : AppCompatActivity() {

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityClockStyleBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClockStyleBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.tabsSubtab

        binding.tabsSubtab.seslSetSubTabStyle();
        binding.tabsSubtab.tabMode = TabLayout.SESL_MODE_WEIGHT_AUTO;
        binding.tabsSubtab.addTab(binding.tabsSubtab.newTab().setText("Select clock"))
        binding.tabsSubtab.addTab(binding.tabsSubtab.newTab().setText("Appearance"))

        binding.tabsBottomnavText.setOnItemSelectedListener { it ->

            when (it.itemId) {
                R.id.bvn_1 -> {
                    showClockStyleHolder()
                }
                R.id.bvn_2 -> {
                    hideClockStyleHolder()
                }
                else -> {}
            }
            false
        }

        binding.clockViewHolder.setOnTouchListener(object : OnSwipeTouchListener(this@ClockStyleActivity) {
            override fun onSwipeTop() {
                showClockStyleHolder()
            }
            override fun onSwipeBottom() {
                hideClockStyleHolder()
            }
        })

    }

    private fun showClockStyleHolder() {
        val isOriginalSize = binding.clockViewHolder.layoutParams?.let { layoutParams ->
            layoutParams.width == originalWidth || layoutParams.width == -1
                    && layoutParams.height == originalHeight || layoutParams.height == 0
        } ?: false
        val resizeAnimation = ResizeAnimation(
            binding.clockViewHolder,
            originalHeight.toFloat(),
            binding.clockViewHolder.height.toFloat(),
            originalWidth.toFloat(),
            binding.clockViewHolder.width.toFloat(),
            300
        )
        binding.clockViewHolder.startAnimation(resizeAnimation)
        if(!isOriginalSize) binding.clockStyleHolder.slideUp()
    }

    private fun hideClockStyleHolder() {
        val isOriginalSize = binding.clockViewHolder.layoutParams?.let { layoutParams ->
            layoutParams.width == originalWidth || layoutParams.width == -1
                    && layoutParams.height == originalHeight || layoutParams.height == 0
        } ?: false
        val resizeAnimation = ResizeAnimation(
            binding.clockViewHolder,
            originalHeight + (originalHeight * 0.15).toInt().toFloat(),
            binding.clockViewHolder.height.toFloat(),
            originalWidth + (originalWidth * 0.15).toInt().toFloat(),
            binding.clockViewHolder.width.toFloat(),
            300
        )
        binding.clockViewHolder.startAnimation(resizeAnimation)
        if(isOriginalSize) binding.clockStyleHolder.slideDown()
    }


    private fun View.slideUp(duration: Int = 300) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }

    private fun View.slideDown(duration: Int = 300) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (originalWidth == 0 && originalHeight == 0) {
            originalWidth = binding.clockViewHolder.width
            originalHeight = binding.clockViewHolder.height
        }
    }

}