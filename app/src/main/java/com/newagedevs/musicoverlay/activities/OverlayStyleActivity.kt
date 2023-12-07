package com.newagedevs.musicoverlay.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.newagedevs.musicoverlay.models.Constants.Companion.visualizerList
import com.newagedevs.musicoverlay.view.ColorPaletteView
import me.bogerchan.niervisualizer.NierVisualizerManager
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType1Renderer
import kotlin.random.Random


@SuppressLint("MissingPermission")
class OverlayStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener, SeslSeekBar.OnSeekBarChangeListener {

    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityOverlayStyleBinding

    private var mCurrentStyleIndex = 0
    private var mVisualizerManager: NierVisualizerManager? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverlayStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.surfaceView.setZOrderOnTop(true)
        binding.surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)

        binding.colorPaletteView.setColorSelectionListener(this)
        binding.transparencySeekBar.setOnSeekBarChangeListener(this)
        binding.transparencySeekBar.max = 100

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        handler.postDelayed(checkMusicRunnable, 10)
        byteArrays = generateRandomByteArray(128)

        binding.tabsBottomnavText.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bvn_1 -> {
                    this.finish()
                }
                R.id.bvn_2 -> {

                }
                else -> {}
            }
            false
        }

        binding.overlayViewHolder.setOnTouchListener(object : OnSwipeTouchListener(this@OverlayStyleActivity) {
            override fun onSwipeTop() {
                showClockStyleHolder()
            }
            override fun onSwipeBottom() {
                hideClockStyleHolder()
            }
        })

        mVisualizerManager = NierVisualizerManager().apply {
            init(object : NierVisualizerManager.NVDataSource {
                override fun getDataSamplingInterval() = 0L
                override fun getDataLength() = byteArrays.size

                override fun fetchFftData(): ByteArray? {
                    return null
                }
                override fun fetchWaveData(): ByteArray {
                    return byteArrays
                }
            })
        }

        binding.changeVisualizerStyle.setOnClickListener {
            val visualizers = visualizerList[++mCurrentStyleIndex % visualizerList.size]
            mVisualizerManager?.start(binding.surfaceView, visualizers)
        }

    }

    private var byteArrays = ByteArray(128)

    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())

    fun generateRandomByteArray(size: Int): ByteArray {
        val byteArray = ByteArray(size)
        Random.nextBytes(byteArray)
        return byteArray
    }

    private val checkMusicRunnable = object : Runnable {
        override fun run() {
            byteArrays = generateRandomByteArray(128)
            handler.postDelayed(this, 10)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVisualizerManager?.release()
        mVisualizerManager = null
        handler.removeCallbacks(checkMusicRunnable)
    }

    private fun showClockStyleHolder() {
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

    private fun hideClockStyleHolder() {
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

        val drawable = GradientDrawable()
        drawable.setColor(color)
        if(binding.transparencySeekBar.progress != 0) {
            drawable.alpha = 300 - (binding.transparencySeekBar.progress * 2.55).toInt()
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
            drawable.alpha = 300 - (binding.transparencySeekBar.progress * 2.55).toInt()
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

    }

    override fun onStartTrackingTouch(seekBar: SeslSeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeslSeekBar?) { }



}