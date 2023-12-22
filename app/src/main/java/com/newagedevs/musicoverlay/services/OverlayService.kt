package com.newagedevs.musicoverlay.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.helper.MediaPlayerManager
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.FrameClockView
import com.newagedevs.musicoverlay.view.HandlerView
import com.newagedevs.musicoverlay.view.TextClockView
import dev.oneuiproject.oneui.widget.Toast
import io.github.jeffshee.visualizer.painters.Painter
import io.github.jeffshee.visualizer.utils.VisualizerHelper
import io.github.jeffshee.visualizer.views.VisualizerView
import kotlin.math.abs
import kotlin.random.Random


class OverlayService : Service(), View.OnClickListener {
    private var wm: WindowManager? = null
    private var overlayView: View? = null


    companion object {
        private const val TAG = "OverlayService"
        private const val CHANNEL_ID = "channel1"
        private const val NOTIFICATION_ID = 1

        private const val TOUCH_MOVE_FACTOR: Long = 20
        private const val TOUCH_TIME_FACTOR: Long = 300
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
    }



    private var positionLocked:Boolean = false
    private var vibrateOnClick:Boolean = false

    private var lastY = 0f
    private var actionDownPoint = PointF(0f, 0f)
    private var touchDownTime = 0L
    private var lastClickTime = 0L


    private val handler = Handler(Looper.getMainLooper())


    private var currentVisualizerIndex = 0
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var helper: VisualizerHelper
    private lateinit var visualizerList: List<Painter?>


    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Overlay Service")
            .setContentText("Tap to manage overlay")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_show, "Show", getPendingIntent("show"))
            .addAction(R.drawable.ic_hide, "Hide", getPendingIntent("hide"))
            .addAction(R.drawable.ic_stop, "Stop", getPendingIntent("stop"))
            .build()


        startForeground(NOTIFICATION_ID, notification)
    }


    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, OverlayService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }


    override fun onClick(v: View?) {
        Toast.makeText(this, "Overlay clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            wm!!.removeView(overlayView)
            overlayView = null

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                "show" -> {
                    if (overlayView == null) {

                        wm = getSystemService(WINDOW_SERVICE) as WindowManager

                        overlayView = LayoutInflater.from(this@OverlayService).inflate(R.layout.overlay_layout, null)

                        val overlayLayout = overlayView?.findViewById<FrameLayout>(R.id.overlay_layout)
                        val overlayViewHolder = overlayView?.findViewById<ConstraintLayout>(R.id.overlay_view_holder)
                        val clockViewHolder = overlayView?.findViewById<LinearLayout>(R.id.clock_view_holder)
                        val textClockView = overlayView?.findViewById<TextClockView>(R.id.textClock_preview)
                        val frameClockView = overlayView?.findViewById<FrameClockView>(R.id.frameClock_preview)
                        val visualizerView = overlayView?.findViewById<VisualizerView>(R.id.visualizer_view)


                        val layoutParams = WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat.TRANSLUCENT
                        )

                        // This flag allows the window to extend outside the screen. Use it with caution.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            layoutParams.layoutInDisplayCutoutMode =
                                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        }

                        // Set flags to draw over status bar and navigation bar
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            layoutParams.flags =
                                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        }

                        overlayView?.systemUiVisibility = (
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                )

                        // load overlay prefs
                        val overlayColor = SharedPrefRepository(this).getOverlayColor()
                        val alpha = SharedPrefRepository(this).getOverlayTransparency()

                        // load handler prefs
                        val handlerIsLockPosition = SharedPrefRepository(this).isLockHandlerPositionEnabled()
                        val handlerIsVibrateOnTouch = SharedPrefRepository(this).isVibrateHandlerOnTouchEnabled()
                        val handlerPosition = SharedPrefRepository(this).getHandlerPosition()
                        val handlerColor = SharedPrefRepository(this).getHandlerColor()
                        val handlerTransparency = SharedPrefRepository(this).getHandlerTransparency()
                        val handlerSize = SharedPrefRepository(this).getHandlerSize()
                        val handlerWidth = SharedPrefRepository(this).getHandlerWidth()
                        val translationY = SharedPrefRepository(this).getHandlerTranslationY()

                        val handlerView = HandlerView(this)
                        handlerView.setOnClickListener {
                            Toast.makeText(this, "Handler clicked", Toast.LENGTH_SHORT).show()
                        }

                        handlerView.setHandlerPositionIsLocked(handlerIsLockPosition)
                        handlerView.setTranslationYPosition(translationY)
                        handlerView.setViewGravity(if (handlerPosition == "Left") Gravity.START else Gravity.END)
                        handlerView.setViewColor(handlerColor, handlerTransparency)
                        handlerView.setViewDimension(Constants.handlerWidthList[handlerWidth], ((handlerSize * 1.5) + 200).toInt())
//                        handlerView.setHandlerPositionChangeListener(this)
                        handlerView.setVibrateOnClick(handlerIsVibrateOnTouch)

                        overlayLayout?.addView(handlerView)

                        // Overlay background d color
                        val drawable = GradientDrawable()
                        drawable.setColor(overlayColor)
                        drawable.alpha = alpha
                        overlayViewHolder?.background = drawable

                        overlayViewHolder?.setOnClickListener(this)

                        clockViewHolder?.setOnTouchListener { _, event ->

                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    lastY = event.rawY
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
                                            }, 15000)
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

                        // Text clock
                        val clockIndex = SharedPrefRepository(this).getClockStyleIndex()
                        val textTransparency = SharedPrefRepository(this).getTextClockTransparency()
                        val frameTransparency = SharedPrefRepository(this).getFrameClockTransparency()
                        val clockColor = SharedPrefRepository(this).getClockColor()

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
                        helper = VisualizerHelper(0)
                        visualizerList = Constants.visualizerList(this)

                        val currentVisualizerIndex = SharedPrefRepository(this).getOverlayStyleIndex()
                        val visualizer = visualizerList[currentVisualizerIndex]

                        visualizerView?.fps = false
                        if(visualizer == null) {
                            visualizerView?.visibility = View.GONE
                        } else {
                            visualizerView?.visibility = View.VISIBLE
                            visualizerView?.setup(helper, visualizer)
                        }

                        wm!!.addView(overlayView, layoutParams)
                    }
                }
                "hide" -> {
                    if (overlayView != null) {
                        wm!!.removeView(overlayView)
                        overlayView = null

                    }
                }
                "stop" -> {
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    private fun now(): Long {
        return SystemClock.elapsedRealtime()
    }

}