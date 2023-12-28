package com.newagedevs.musicoverlay.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.models.UnlockCondition
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.FrameClockView
import com.newagedevs.musicoverlay.view.HandlerView
import com.newagedevs.musicoverlay.view.TextClockView
import io.github.jeffshee.visualizer.painters.Painter
import io.github.jeffshee.visualizer.utils.VisualizerHelper
import io.github.jeffshee.visualizer.views.VisualizerView
import kotlin.math.abs
import kotlin.math.sqrt


interface OverlayServiceInterface {
    fun show()
    fun hide()
    fun update()
}


class OverlayService : Service(), OverlayServiceInterface {


    private val binder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun instance(): OverlayServiceInterface = this@OverlayService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private var overlayView: View? = null
    private var handlerView: HandlerView? = null

    private var windowManager: WindowManager? = null
    private var audioManager: AudioManager? = null

    private var lockScreenUtil: LockScreenUtil? = null

    private var minSwipeY: Float = 0f

    companion object {

        private const val TAG = "OverlayService"
        private const val CHANNEL_ID = "channel1"
        private const val NOTIFICATION_ID = 1

        private const val TOUCH_MOVE_FACTOR: Long = 20
        private const val TOUCH_TIME_FACTOR: Long = 300
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
        private const val LONG_PRESS_TIME_THRESHOLD: Long = 500

        fun start(context: Context) {
            try{
                if (Settings.canDrawOverlays(context)) {
                    val intent = Intent(context, OverlayService::class.java)
                    context.startForegroundService(intent)
                }
            } catch (_:Exception) { }
        }

        fun stop(context: Context) {
            try{
                if (Settings.canDrawOverlays(context)) {
                    val intent = Intent(context, OverlayService::class.java).apply {
                        putExtra("command", "stop")
                    }
                    context.stopService(intent)
                }
            } catch (_:Exception) { }
        }

        private var volume: Int = 0
    }

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private val handler = Handler(Looper.getMainLooper())

    private var helper: VisualizerHelper? = null
    private val longPressHandler = Handler(Looper.getMainLooper())

    private lateinit var visualizerList: List<Painter?>


    private var eventX1: Float = 0f
    private var eventX2: Float = 0f

    private var startY: Float = 0f

    private var actionDownPoint = PointF(0f, 0f)
    private var previousPoint = PointF(0f, 0f)

    private var touchDownTime = 0L
    private var lastClickTime = 0L

    // Long press
    private var longPressedRunnable = java.lang.Runnable {
        onLongPress()
        isLongPressHandlerActivated = true
    }

    private var isLongPressHandlerActivated = false

    private var isActionMoveEventStored = false
    private var lastActionMoveEventBeforeUpX = 0f
    private var lastActionMoveEventBeforeUpY = 0f

    override fun onCreate() {
        super.onCreate()

        try {
            helper = VisualizerHelper(0)
        } catch (_: Exception) {}

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        lockScreenUtil = LockScreenUtil(this)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_layer)
            .setContentTitle("Overlay Service")
            .setContentText("Tap to manage overlay")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_show, "Show", getPendingIntent("show"))
            .addAction(R.drawable.ic_hide, "Hide", getPendingIntent("hide"))
            .addAction(R.drawable.ic_power, "Stop", getPendingIntent("stop"))
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, OverlayService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlayView()
        hideHandlerView()
        try {
            helper?.release()
        } catch (_: Exception) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                "show" -> {
                    createOverlayHandler()
                }
                "hide" -> {
                    val isOverlayViewNull = overlayView != null
                    hideOverlayView()
                    hideHandlerView()
                    shouldLock(isOverlayViewNull)
                    return START_STICKY
                }
                "stop" -> {
                    shouldLock(overlayView != null)
                    SharedPrefRepository(this).setRunning(false)
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    private fun now(): Long {
        return SystemClock.elapsedRealtime()
    }


    private fun createOverlayHandler() {
        if (handlerView == null) {
            // load prefs
            val handlerIsLockPosition = SharedPrefRepository(this).isLockHandlerPositionEnabled()
            val handlerIsVibrateOnTouch = SharedPrefRepository(this).isVibrateHandlerOnTouchEnabled()
            val handlerPosition = SharedPrefRepository(this).getHandlerPosition()
            val handlerColor = SharedPrefRepository(this).getHandlerColor()
            val handlerTransparency = SharedPrefRepository(this).getHandlerTransparency()
            val handlerSize = SharedPrefRepository(this).getHandlerSize()
            val handlerWidth = SharedPrefRepository(this).getHandlerWidth()
            val translationY = SharedPrefRepository(this).getHandlerTranslationY()


            val layoutParams = WindowManager.LayoutParams(
                Constants.handlerWidthList[handlerWidth],
                ((handlerSize * 1.5) + 200).toInt(),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                x = 0
                y = translationY.toInt()
                gravity = Gravity.TOP or if (handlerPosition == "Left") Gravity.START else Gravity.END
            }


            handlerView = HandlerView(this)

            handlerView?.setHandlerPositionIsLocked(true)
            handlerView?.setTranslationYPosition(0f)
            handlerView?.setViewGravity(if (handlerPosition == "Left") Gravity.START else Gravity.END)
            handlerView?.setViewColor(handlerColor, handlerTransparency)
            handlerView?.setViewDimension(Constants.handlerWidthList[handlerWidth], ((handlerSize * 1.5) + 200).toInt())
            handlerView?.setVibrateOnClick(handlerIsVibrateOnTouch)
            handlerView?.setHandlerPositionChangeListener(object : HandlerView.HandlerPositionChangeListener {
                override fun onVertical(rawY: Float) { }

                override fun onVertical(rawY: Int) {
                    if(!handlerIsLockPosition) {
                        layoutParams.y = layoutParams.y + rawY
                        windowManager?.updateViewLayout(handlerView, layoutParams)
                        SharedPrefRepository(this@OverlayService).setHandlerTranslationY(layoutParams.y.toFloat())
                    }
                }
            })

            handlerView?.setOnClickListener {
                hideHandlerView()
                createOverlayView()
            }

            windowManager?.addView(handlerView, layoutParams)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    @Suppress("DEPRECATION")
    private fun createOverlayView() {
        if (overlayView == null) {
            // load prefs
            val overlayColor = SharedPrefRepository(this).getOverlayColor()
            val alpha = SharedPrefRepository(this).getOverlayTransparency()
            val clockIndex = SharedPrefRepository(this).getClockStyleIndex()
            val textTransparency = SharedPrefRepository(this).getTextClockTransparency()
            val frameTransparency = SharedPrefRepository(this).getFrameClockTransparency()
            val clockColor = SharedPrefRepository(this).getClockColor()
            val currentVisualizerIndex = SharedPrefRepository(this).getOverlayStyleIndex()

            val unlockCondition = SharedPrefRepository(this).getUnlockCondition()
            val shouldShowAlwaysOnDisplay = SharedPrefRepository(this).isAlwaysOnDisplay()

            val shouldIncreaseVolume = SharedPrefRepository(this).isGestureIncreaseVolumeEnabled()
            val shouldDecreaseVolume = SharedPrefRepository(this).isGestureDecreaseVolumeEnabled()


            overlayView = LayoutInflater.from(this@OverlayService).inflate(R.layout.overlay_layout, null)
            overlayView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

            val overlayViewHolder = overlayView?.findViewById<ConstraintLayout>(R.id.overlay_view_holder)
            val clockViewHolder = overlayView?.findViewById<LinearLayout>(R.id.clock_view_holder)
            val textClockView = overlayView?.findViewById<TextClockView>(R.id.textClock_preview)
            val frameClockView = overlayView?.findViewById<FrameClockView>(R.id.frameClock_preview)
            val visualizerView = overlayView?.findViewById<VisualizerView>(R.id.visualizer_view)

            // Overlay background d color
            val drawable = GradientDrawable()
            drawable.setColor(overlayColor)
            drawable.alpha = alpha
            overlayViewHolder?.background = drawable

            clockViewHolder?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        actionDownPoint = PointF(event.x, event.y)
                        touchDownTime = now()
                    }
                    MotionEvent.ACTION_MOVE -> {

                    }
                    MotionEvent.ACTION_UP -> {
                        val isTouchDuration = now() - touchDownTime < TOUCH_TIME_FACTOR
                        val isTouchLength = abs(event.x - actionDownPoint.x) + abs(event.y - actionDownPoint.y) < TOUCH_MOVE_FACTOR
                        val shouldClick = isTouchLength && isTouchDuration

                        if (shouldClick) {
                            if (now() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                                // Double click
                                clockViewHolder.visibility = View.GONE
                                handler.postDelayed({
                                    clockViewHolder.visibility = View.VISIBLE
                                }, 5000)
                                lastClickTime = 0
                            } else {
                                // Single click
                                lastClickTime = now()
                            }
                        }
                    }
                }

                return@setOnTouchListener true
            }

            overlayViewHolder?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        longPressHandler.postDelayed(longPressedRunnable, LONG_PRESS_TIME_THRESHOLD)
                        actionDownPoint = PointF(event.x, event.y)
                        previousPoint = PointF(event.x, event.y)
                        touchDownTime = now()
                        eventX1 = event.x
                        startY = event.y

                        minSwipeY = 0f

                        lastX = event.x
                        lastY = event.y

                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_HOVER_MOVE -> {
                        if (!isActionMoveEventStored) {
                            isActionMoveEventStored = true
                            lastActionMoveEventBeforeUpX = event.x
                            lastActionMoveEventBeforeUpY = event.y
                        } else {
                            val currentX = event.x
                            val currentY = event.y
                            val firstX = lastActionMoveEventBeforeUpX
                            val firstY = lastActionMoveEventBeforeUpY
                            val distance = sqrt(((currentY - firstY) * (currentY - firstY) + (currentX - firstX) * (currentX - firstX)).toDouble())

                            if (distance > 20) {
                                longPressHandler.removeCallbacks(longPressedRunnable)
                                eventX2 = event.x
                                previousPoint = PointF(event.x, event.y)
                            }

                            val x = event.x
                            val y = event.y
                            val distanceX = x - lastX
                            val distanceY = y - lastY

                            minSwipeY += distanceY

                            val sWidth = Resources.getSystem().displayMetrics.widthPixels
                            val sHeight = Resources.getSystem().displayMetrics.heightPixels

                            val border = 100 * Resources.getSystem().displayMetrics.density.toInt()
                            if(event.x < border || event.y < border || event.x > sWidth - border || event.y > sHeight - border)
                                return@setOnTouchListener false

                            if(abs(distanceX) < abs(distanceY) && abs(minSwipeY) > 30){
                                val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

                                val newValue = if(distanceY > 0) {
                                    if(shouldDecreaseVolume) volume - 1 else volume
                                } else {
                                    if(shouldIncreaseVolume) volume + 1 else volume
                                }

                                if(newValue in 0..maxVolume) volume = newValue
                                if(shouldDecreaseVolume || shouldIncreaseVolume){
                                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
                                }

                                minSwipeY = 0f
                            }
                            lastX = x
                            lastY = y
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        isActionMoveEventStored = false
                        longPressHandler.removeCallbacks(longPressedRunnable);
                        if(isLongPressHandlerActivated) {
                            isLongPressHandlerActivated = false
                            return@setOnTouchListener false
                        }

                        val isTouchDuration = now() - touchDownTime < TOUCH_TIME_FACTOR
                        val isTouchLength = abs(event.x - actionDownPoint.x) + abs(event.y - actionDownPoint.y) < TOUCH_MOVE_FACTOR
                        val shouldClick = isTouchLength && isTouchDuration

                        if (shouldClick) {
                            lastClickTime = if (now() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                                // Double click
                                if(unlockCondition == UnlockCondition.DOUBLE_TAP.displayText) {
                                    hideOverlayView()
                                    createOverlayHandler()
                                    shouldLock()
                                }
                                0
                            } else {
                                // Single click
                                if(unlockCondition == UnlockCondition.TAP.displayText) {
                                    hideOverlayView()
                                    createOverlayHandler()
                                    shouldLock()
                                }
                                now()
                            }
                        }
                    }
                }

                return@setOnTouchListener false
            }


            // Text clock
            val clock = Constants.clockList[clockIndex]
            when(clock.viewType) {
                ClockViewType.TEXT_CLOCK.ordinal -> {
                    textClockView?.visibility = View.VISIBLE
                    frameClockView?.visibility = View.GONE
                    textClockView?.setAttributes(clock)
                }
                ClockViewType.FRAME_CLOCK.ordinal -> {
                    textClockView?.visibility = View.GONE
                    frameClockView?.visibility = View.VISIBLE
                    frameClockView?.setAttributes(clock)
                }
            }
            textClockView?.setHourTextSize(100f)
            textClockView?.setMinuteTextSize(100f)
            textClockView?.setOpacity(textTransparency)
            frameClockView?.setOpacity(frameTransparency)
            clockColor?.let { it2 -> textClockView?.setForegroundColor(it2) }
            clockColor?.let { it2 -> frameClockView?.setForegroundColor(it2) }


            // Visualizer View Settings
            visualizerList = Constants.visualizerList(this)
            val visualizer = visualizerList[currentVisualizerIndex]

            visualizerView?.fps = false
            if(visualizer == null) {
                visualizerView?.visibility = View.GONE
            } else {
                visualizerView?.visibility = View.VISIBLE
                helper.let {
                    if (it != null) {
                        visualizerView?.setup(it, visualizer)
                    }
                }
            }

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    flags =
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                }
            }

            windowManager!!.addView(overlayView, layoutParams)

            if(shouldShowAlwaysOnDisplay) {
                clockViewHolder?.visibility = View.GONE
                handler.postDelayed({
                    clockViewHolder?.visibility = View.VISIBLE
                }, 5000)
            } else {
                clockViewHolder?.visibility = View.GONE
            }

        }
    }

    private fun onLongPress() {
        when (SharedPrefRepository(this@OverlayService).getUnlockCondition()) {
            UnlockCondition.LONG_PRESS.displayText -> {
                hideOverlayView()
                createOverlayHandler()
                shouldLock()
            }
        }
    }

    private fun hideOverlayView() {
        overlayView?.let { oView ->
            windowManager?.removeView(oView)
            overlayView = null
        }
    }

    private fun hideHandlerView() {
        handlerView?.let { hView ->
            windowManager?.removeView(hView)
            handlerView = null
        }
    }

    private fun shouldLock(value: Boolean = true) {
        if(SharedPrefRepository(this@OverlayService).isScreenLockPrivacyEnabled() && value) {
            lockScreenUtil?.lockScreen()
        }
    }

    override fun show() {
        createOverlayHandler()
    }

    override fun hide() {
        hideOverlayView()
        hideHandlerView()
    }

    override fun update() {

    }
}