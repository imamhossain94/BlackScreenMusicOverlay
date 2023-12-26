package com.newagedevs.musicoverlay.activities

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslSeekBar
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.databinding.ActivityOverlayStyleBinding
import com.newagedevs.musicoverlay.extension.OnSwipeTouchListener
import com.newagedevs.musicoverlay.extension.ResizeAnimation
import com.newagedevs.musicoverlay.helper.MediaPlayerManager
import com.newagedevs.musicoverlay.models.Constants.Companion.visualizerList
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.ColorPaletteView
import io.github.jeffshee.visualizer.painters.Painter
import io.github.jeffshee.visualizer.utils.VisualizerHelper


@SuppressLint("MissingPermission")
class OverlayStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener, SeslSeekBar.OnSeekBarChangeListener {

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityOverlayStyleBinding

    private var currentVisualizerIndex = 0
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var helper: VisualizerHelper
    private lateinit var visualizerList: List<Painter?>


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverlayStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.colorPaletteView.setColorSelectionListener(this)
        binding.transparencySeekBar.setOnSeekBarChangeListener(this)
        binding.transparencySeekBar.max = 100

        val overlayColor = SharedPrefRepository(this).getOverlayColor()
        overlayColor.let { binding.colorPaletteView.setDefaultSelectedColor(it) }
        val alpha = SharedPrefRepository(this).getOverlayTransparency()
        binding.transparencySeekBar.progress = ((255 - alpha) / 2.55).toInt()

        currentVisualizerIndex = SharedPrefRepository(this).getOverlayStyleIndex()

        binding.tabsBottomnavText.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bvn_1 -> {
                    finish()
                }
                R.id.bvn_2 -> {
                    hideOverlayStyleHolder()
                }
                else -> {}
            }
            false
        }

        binding.overlayViewHolder.setOnTouchListener(object : OnSwipeTouchListener(this@OverlayStyleActivity) {
            override fun onSwipeTop() {
                showOverlayStyleHolder()
            }
            override fun onSwipeBottom() {
                hideOverlayStyleHolder()
            }
        })

        binding.changeVisualizerStyle.setOnClickListener {
            currentVisualizerIndex %= visualizerList.size
            val visualizer = visualizerList[currentVisualizerIndex]

            if(visualizer == null) {
                mediaPlayerManager.pause()
                binding.visualizerView.visibility = View.GONE
            } else {
                mediaPlayerManager.reset().play()
                binding.visualizerView.visibility = View.VISIBLE
                binding.visualizerView.setup(helper, visualizer)
            }

            val msg = if (visualizer == null) "None" else "${currentVisualizerIndex}/${visualizerList.lastIndex}"
            binding.visualizerStyleStatus.text = msg
            SharedPrefRepository(this).setOverlayStyleIndex(currentVisualizerIndex)

            ++currentVisualizerIndex
        }

        initVisualizer()
    }

    override fun onDestroy() {
        super.onDestroy()
        helper.release()
        mediaPlayerManager.release()
    }

    private fun showOverlayStyleHolder() {
        val isOriginalSize = binding.overlayViewHolder.layoutParams?.let { layoutParams ->
            layoutParams.width == originalWidth || layoutParams.width == -1
                    && layoutParams.height == originalHeight || layoutParams.height == 0
        } ?: false
        val resizeAnimation = ResizeAnimation(
            binding.overlayViewHolder,
            originalHeight.toFloat(),
            binding.overlayViewHolder.height.toFloat(),
            originalWidth.toFloat(),
            binding.overlayViewHolder.width.toFloat(),
            300
        )
        binding.overlayViewHolder.startAnimation(resizeAnimation)
        if(!isOriginalSize) binding.overlayStyleHolder.slideUp()
    }

    private fun hideOverlayStyleHolder() {
        val isOriginalSize = binding.overlayViewHolder.layoutParams?.let { layoutParams ->
            layoutParams.width == originalWidth || layoutParams.width == -1
                    && layoutParams.height == originalHeight || layoutParams.height == 0
        } ?: false
        val resizeAnimation = ResizeAnimation(
            binding.overlayViewHolder,
            originalHeight + (originalHeight * 0.15).toInt().toFloat(),
            binding.overlayViewHolder.height.toFloat(),
            originalWidth + (originalWidth * 0.15).toInt().toFloat(),
            binding.overlayViewHolder.width.toFloat(),
            300
        )
        binding.overlayViewHolder.startAnimation(resizeAnimation)
        if(isOriginalSize) binding.overlayStyleHolder.slideDown()
        else finish()
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
            originalWidth = binding.overlayViewHolder.width
            originalHeight = binding.overlayViewHolder.height
        }
    }

    override fun onColorSelected(color: Int) {
        binding.overlayViewHolder.background = createDrawableWithColor(color)
        SharedPrefRepository(this).setOverlayColor(color)

        val drawable = GradientDrawable()
        drawable.setColor(color)
        if(binding.transparencySeekBar.progress != 0) {
            val alpha = 255 - (binding.transparencySeekBar.progress * 2.55).toInt()
            drawable.alpha = alpha
            SharedPrefRepository(this).setOverlayTransparency(alpha)
        }
    }

    // Function to create a GradientDrawable with a specific color
    private fun createDrawableWithColor(color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)

        val typedValue = TypedValue()
        this.theme.resolveAttribute(
            R.attr.colorSecondaryText,
            typedValue,
            true
        )

        drawable.setStroke(2.dpToPx(), ContextCompat.getColor(this, typedValue.resourceId))
        drawable.cornerRadius = 16.dpToPx().toFloat()

        if(binding.transparencySeekBar.progress != 0) {
            val alpha = 255 - (binding.transparencySeekBar.progress * 2.55).toInt()
            drawable.alpha = alpha
            SharedPrefRepository(this).setOverlayTransparency(alpha)
        }

        return drawable
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    override fun onProgressChanged(seekBar: SeslSeekBar?, progress: Int, fromUser: Boolean) {
        binding.overlayViewHolder.background.apply {
            alpha = 300 - (progress * 2.55).toInt()
        }
        SharedPrefRepository(this).setOverlayTransparency(300 - (progress * 2.55).toInt())
    }

    override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }

    private fun initVisualizer() {
        binding.visualizerView.fps = false
        mediaPlayerManager = MediaPlayerManager(this)
        helper = VisualizerHelper(mediaPlayerManager.play().pause().getSessionId())

        visualizerList = visualizerList(this, topMargin = -0.47f, bottomMargin = 0.4f)

        val visualizer = visualizerList[currentVisualizerIndex]
        if(visualizer == null) {
            binding.visualizerView.visibility = View.GONE
        } else {
            mediaPlayerManager.reset().play()
            binding.visualizerView.visibility = View.VISIBLE
            binding.visualizerView.setup(helper, visualizer)
        }
        val msg = if (visualizer == null) "None" else "${currentVisualizerIndex}/${visualizerList.lastIndex}"
        binding.visualizerStyleStatus.text = msg
    }

}