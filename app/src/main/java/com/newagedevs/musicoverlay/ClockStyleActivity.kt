package com.newagedevs.musicoverlay

import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.newagedevs.musicoverlay.databinding.ActivityClockStyleBinding
import com.newagedevs.musicoverlay.extension.ResizeAnimation

class ClockStyleActivity : AppCompatActivity() {

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityClockStyleBinding
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
                    binding.clockStyleHolder.slideUp()
                    val resizeAnimation = ResizeAnimation(
                        binding.clockViewHolder,
                        (originalHeight * 0.85).toInt().toFloat(),
                        binding.clockViewHolder.height.toFloat(),
                        (originalWidth * 0.85).toInt().toFloat(),
                        binding.clockViewHolder.width.toFloat(),
                        300 // Duration in milliseconds
                    )
                    binding.clockViewHolder.startAnimation(resizeAnimation)
                }
                R.id.bvn_2 -> {
                    binding.clockStyleHolder.slideDown()
                    val resizeAnimation = ResizeAnimation(
                        binding.clockViewHolder,
                        originalHeight.toFloat(),
                        binding.clockViewHolder.height.toFloat(),
                        originalWidth.toFloat(),
                        binding.clockViewHolder.width.toFloat(),
                        300 // Duration in milliseconds
                    )
                    binding.clockViewHolder.startAnimation(resizeAnimation)
                }
                else -> {}
            }
            false
        }



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