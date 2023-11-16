package com.newagedevs.musicoverlay

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.converter.AbsAudioDataConverter
import com.newagedevs.musicoverlay.converter.AudioDataConverterFactory
import com.newagedevs.musicoverlay.databinding.ActivityOverlayStyleBinding
import com.newagedevs.musicoverlay.extension.OnSwipeTouchListener
import com.newagedevs.musicoverlay.extension.ResizeAnimation
import me.bogerchan.niervisualizer.NierVisualizerManager
import me.bogerchan.niervisualizer.renderer.IRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleSolidRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleWaveRenderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType1Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType2Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType3Renderer
import me.bogerchan.niervisualizer.renderer.line.LineRenderer
import me.bogerchan.niervisualizer.renderer.other.ArcStaticRenderer
import me.bogerchan.niervisualizer.util.NierAnimator

@SuppressLint("MissingPermission")
class OverlayStyleActivity : AppCompatActivity(), AudioStatusListener {
    private lateinit var audioStatusChecker: AudioStatusChecker



    private var originalWidth: Int = 0
    private var originalHeight: Int = 0
    private lateinit var binding: ActivityOverlayStyleBinding

    private var mCurrentStyleIndex = 0
    private var mVisualizerManager: NierVisualizerManager? = null
    private val mRenderers = arrayOf<Array<IRenderer>>(
        arrayOf(ColumnarType1Renderer()),
        arrayOf(ColumnarType2Renderer()),
        arrayOf(ColumnarType3Renderer()),
        //arrayOf(ColumnarType4Renderer()),
        arrayOf(LineRenderer(true)),
        arrayOf(CircleBarRenderer()),
        arrayOf(CircleRenderer(true)),
//        arrayOf(
//            CircleRenderer(true),
//            CircleBarRenderer(),
//            ColumnarType4Renderer()
//        )
        arrayOf(CircleRenderer(true), CircleBarRenderer(), LineRenderer(true)),
        arrayOf(
            ArcStaticRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#cfa9d0fd")
                }),
            ArcStaticRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#dad2eafe")
                },
                amplificationOuter = .83f,
                startAngle = -90f,
                sweepAngle = 225f
            ),
            ArcStaticRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#7fa9d0fd")
                },
                amplificationOuter = .93f,
                amplificationInner = 0.8f,
                startAngle = -45f,
                sweepAngle = 135f
            ),
            CircleSolidRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#d2eafe")
                },
                amplification = .45f
            ),
            CircleBarRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    strokeWidth = 4f
                    color = Color.parseColor("#efe3f2ff")
                },
                modulationStrength = 1f,
                type = CircleBarRenderer.Type.TYPE_A_AND_TYPE_B,
                amplification = 1f, divisions = 8
            ),
            CircleBarRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    strokeWidth = 5f
                    color = Color.parseColor("#e3f2ff")
                },
                modulationStrength = 0.1f,
                amplification = 1.2f,
                divisions = 8
            ),
            CircleWaveRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    strokeWidth = 6f
                    color = Color.WHITE
                },
                modulationStrength = 0.2f,
                type = CircleWaveRenderer.Type.TYPE_B,
                amplification = 1f,
                animator = NierAnimator(
                    interpolator = LinearInterpolator(),
                    duration = 20000,
                    values = floatArrayOf(0f, -360f)
                )
            ),
            CircleWaveRenderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    strokeWidth = 6f
                    color = Color.parseColor("#7fcee7fe")
                },
                modulationStrength = 0.2f,
                type = CircleWaveRenderer.Type.TYPE_B,
                amplification = 1f,
                divisions = 8,
                animator = NierAnimator(
                    interpolator = LinearInterpolator(),
                    duration = 20000,
                    values = floatArrayOf(0f, -360f)
                )
            )
        )
    )

//    private val mPlayer by lazy {
//        MediaPlayer().apply {
//            resources.openRawResourceFd(R.raw.demo_audio).apply {
//                setDataSource(fileDescriptor, startOffset, length)
//            }
//        }
//    }

    private val mAudioBufferSize by lazy {
        AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
    }

    private val mAudioRecord by lazy {
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            mAudioBufferSize
        )
    }

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

//        mPlayer.apply {
//            prepare()
//            start()
//            createNewVisualizerManager()
//        }

        audioStatusChecker = AudioStatusChecker(this, this)
        audioStatusChecker.startChecking()

        mVisualizerManager?.start(binding.surfaceView, mRenderers[++mCurrentStyleIndex % mRenderers.size])


        binding.changeVisualizerStyle.setOnClickListener {
            mVisualizerManager?.start(binding.surfaceView, mRenderers[++mCurrentStyleIndex % mRenderers.size])
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mVisualizerManager?.release()
        mVisualizerManager = null
        //mPlayer.release()
        mAudioRecord.release()
        audioStatusChecker.stopChecking()
    }

    private fun createNewVisualizerManager() {
        mVisualizerManager?.release()
        mVisualizerManager = NierVisualizerManager().apply {
            init(object : NierVisualizerManager.NVDataSource {

                private val mBuffer: ByteArray = ByteArray(512)
                private val mAudioDataConverter: AbsAudioDataConverter =
                    AudioDataConverterFactory.getConverterByAudioRecord(mAudioRecord)

                override fun getDataSamplingInterval() = 0L

                override fun getDataLength() = mBuffer.size

                override fun fetchFftData(): ByteArray? {
                    return null
                }

                override fun fetchWaveData(): ByteArray? {
                    mAudioDataConverter.convertWaveDataTo(mBuffer)
                    return mBuffer
                }

            })
        }
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

    override fun onAudioStatusChanged(isAudioActive: Boolean) {

        Log.d("onAudioStatus", "$isAudioActive")

        if (isAudioActive) {
            mAudioRecord.apply {
                startRecording()
                createNewVisualizerManager()
            }
        } else {
//            mAudioRecord.apply {
//                stop()
//                mVisualizerManager?.stop()
//            }
        }
    }


}