package com.newagedevs.musicoverlay.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.adapter.ViewPagerAdapter
import com.newagedevs.musicoverlay.databinding.ActivityClockStyleBinding
import com.newagedevs.musicoverlay.extension.OnSwipeTouchListener
import com.newagedevs.musicoverlay.extension.ResizeAnimation
import com.newagedevs.musicoverlay.fragment.AppearanceFragment
import com.newagedevs.musicoverlay.fragment.ClocksFragment
import com.newagedevs.musicoverlay.models.Constants

class ClockStyleActivity : AppCompatActivity() {

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    lateinit var binding: ActivityClockStyleBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClockStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabsSubtab

        binding.tabsSubtab.seslSetSubTabStyle()
        binding.tabsSubtab.tabMode = TabLayout.SESL_MODE_WEIGHT_AUTO
        binding.tabsSubtab.addTab(binding.tabsSubtab.newTab().setText("Select clock"))
        binding.tabsSubtab.addTab(binding.tabsSubtab.newTab().setText("Appearance"))

        binding.tabsBottomnavText.setOnItemSelectedListener {

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

        binding.textClockPreview.setHourTextSize(100f)
        binding.textClockPreview.setMinuteTextSize(100f)
        binding.clockViewHolder.setOnTouchListener(object : OnSwipeTouchListener(this@ClockStyleActivity) {
            override fun onSwipeTop() {
                showClockStyleHolder()
            }
            override fun onSwipeBottom() {
                hideClockStyleHolder()
            }
        })

        val adapter = ViewPagerAdapter(this@ClockStyleActivity)

        adapter.addFragment( ClocksFragment(),"Select Clock")
        adapter.addFragment( AppearanceFragment(),"Appearance")
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = adapter
        viewPager.currentItem = 0

        val tabs: TabLayout = binding.tabsSubtab
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

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
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
        animate.duration = duration.toLong()
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation?) { }
        })
        this.startAnimation(animate)
    }

    private fun View.slideDown(duration: Int = 300) {
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
        animate.duration = duration.toLong()
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) { }
        })
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