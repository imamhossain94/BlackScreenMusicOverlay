package com.newagedevs.musicoverlay.activities

//import java.util.Random

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslSeekBar
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.databinding.ActivityOverlayStyleBinding
import com.newagedevs.musicoverlay.extension.OnSwipeTouchListener
import com.newagedevs.musicoverlay.extension.ResizeAnimation
import com.newagedevs.musicoverlay.view.ColorPaletteView
import me.bogerchan.niervisualizer.NierVisualizerManager
import me.bogerchan.niervisualizer.renderer.IRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleSolidRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleWaveRenderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType1Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType2Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType3Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType4Renderer
import me.bogerchan.niervisualizer.renderer.line.LineRenderer
import me.bogerchan.niervisualizer.renderer.other.ArcStaticRenderer
import me.bogerchan.niervisualizer.util.NierAnimator
import kotlin.random.Random


@SuppressLint("MissingPermission")
class OverlayStyleActivity : AppCompatActivity(), ColorPaletteView.ColorSelectionListener, SeslSeekBar.OnSeekBarChangeListener {


    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityOverlayStyleBinding

    private var mCurrentStyleIndex = 0
    private var mVisualizerManager: NierVisualizerManager? = null

    private val mRenderers = arrayOf<Array<IRenderer>>(
        arrayOf(ColumnarType1Renderer()),
        arrayOf(ColumnarType2Renderer()),
        arrayOf(ColumnarType3Renderer()),
        arrayOf(LineRenderer(true)),
        arrayOf(CircleRenderer(true)),
        arrayOf(CircleRenderer(true), CircleBarRenderer(), LineRenderer(true))
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverlayStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabsBottomnavText.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.bvn_1 -> {
                    this.finish()
                }
                R.id.bvn_2 -> {
                    mVisualizerManager?.start(binding.surfaceView, mRenderers[++mCurrentStyleIndex % mRenderers.size])
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

        binding.surfaceView.setZOrderOnTop(true)
        binding.surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)

        binding.changeVisualizerStyle.setOnClickListener {
            mVisualizerManager?.start(binding.surfaceView, mRenderers[++mCurrentStyleIndex % mRenderers.size])
        }

        binding.colorPaletteView.setColorSelectionListener(this)
        binding.transparencySeekBar.setOnSeekBarChangeListener(this)
        binding.transparencySeekBar.max = 100


        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Start checking every 2 seconds v
        handler.postDelayed(checkMusicRunnable, 10)

        byteArrays = generateRandomByteArray(128)

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

        mVisualizerManager?.start(binding.surfaceView, arrayOf(ColumnarType1Renderer()))

    }

    private var byteArrays = ByteArray(128)

    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())

    fun generateRandomByteArray(size: Int): ByteArray {
        val byteArray = ByteArray(size)
        Random.nextBytes(byteArray)
        return byteArray
    }

//    private val checkMusicRunnable = object : Runnable {
//        override fun run() {
//            byteArrays = if (audioManager.isMusicActive) {
//                generateRandomByteArray(512)
//            } else {
//                ByteArray(512)
//            }
//            handler.postDelayed(this, 10)
//        }
//    }

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
            originalWidth = binding.overlayViewHolder.width
            originalHeight = binding.overlayViewHolder.height
        }
    }

    override fun onColorSelected(color: Int) {
        binding.overlayViewHolder.background = createDrawableWithColor(color)
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