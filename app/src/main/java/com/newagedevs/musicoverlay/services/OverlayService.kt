package com.newagedevs.musicoverlay.services

import android.R.attr.height
import android.R.attr.width
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.app.NotificationCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.view.FrameClockView
import com.newagedevs.musicoverlay.view.HandlerView
import com.newagedevs.musicoverlay.view.TextClockView
import dev.oneuiproject.oneui.widget.Toast
import me.bogerchan.niervisualizer.NierVisualizerManager
import kotlin.random.Random


//
//class OverlayService : Service() {
//
//    private var overlayView: View? = null
//    private lateinit var windowManager: WindowManager
//
//    companion object {
//        private const val CHANNEL_ID = "OverlayServiceChannel"
//        private const val NOTIFICATION_ID = 1
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//        showNotification()
//        createOverlayView()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        hideNotification()
//        removeOverlayView()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        intent?.action?.let {
//            when (it) {
//                "show" -> showOverlay()
//                "hide" -> hideOverlay()
//                "stop" -> stopSelf()
//            }
//        }
//        return START_STICKY
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Overlay Service Channel"
//            val descriptionText = "Channel for Overlay Service"
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun showNotification() {
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle("Overlay Service")
//            .setContentText("Tap to manage overlay")
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setAutoCancel(false)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(R.drawable.ic_show, "Show", getPendingIntent("show"))
//            .addAction(R.drawable.ic_hide, "Hide", getPendingIntent("hide"))
//            .addAction(R.drawable.ic_stop, "Stop", getPendingIntent("stop"))
//            .build()
//
//        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
//        startForeground(NOTIFICATION_ID, notification)
//    }
//
//    private fun hideNotification() {
//        stopForeground(true)
//        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
//    }
//
//    private fun getPendingIntent(action: String): PendingIntent {
//        val intent = Intent(this, OverlayService::class.java).apply {
//            this.action = action
//        }
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//    }
//
//    private fun createOverlayView(): View? {
//        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            else
//                WindowManager.LayoutParams.TYPE_PHONE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT
//        )
//        params.gravity = Gravity.CENTER
//
//        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        windowManager.addView(overlayView, params)
//
//        return overlayView
//    }
//
//    private fun removeOverlayView() {
//        windowManager.removeView(overlayView)
//    }
//
//    private fun showOverlay() {
//        if (overlayView == null) {
//            // Create the overlay view
//            overlayView = createOverlayView()
//        }
//    }
//
//    private fun hideOverlay() {
//        overlayView?.let {
//            // Remove the overlay view from the root layout
//            windowManager.removeView(it)
//            overlayView = null
//        }
//    }
//}



class OverlayService : Service(), View.OnClickListener {
    private var wm: WindowManager? = null
    private var overlayView: FrameLayout? = null
    private var surfaceView: SurfaceView? = null
    private var overlayIndex = 0


    private var mVisualizerManager: NierVisualizerManager? = null
    private var byteArrays = ByteArray(128)

    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "OverlayService"
        private const val CHANNEL_ID = "channel1"
        private const val NOTIFICATION_ID = 1
    }

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
            mVisualizerManager?.release()
            mVisualizerManager = null
            handler.removeCallbacks(checkMusicRunnable)

            wm!!.removeView(overlayView)
            overlayView = null
            surfaceView = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                "show" -> {
                    if (overlayView == null) {

                        wm = getSystemService(WINDOW_SERVICE) as WindowManager

                        overlayView = FrameLayout(this@OverlayService)
                        overlayView?.setBackgroundColor(Color.BLACK)

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

                        layoutParams.apply {
                            gravity = Gravity.CENTER
                        }

                        overlayView?.systemUiVisibility = (
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                )

                        overlayView!!.setOnClickListener(this)


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



                        // Text clock
                        val clockIndex = SharedPrefRepository(this).getClockStyleIndex()
                        val textTransparency = SharedPrefRepository(this).getTextClockTransparency()
                        val frameTransparency = SharedPrefRepository(this).getFrameClockTransparency()
                        val clockColor = SharedPrefRepository(this).getClockColor()

                        val textClockView = TextClockView(this)
                        val frameClockView = FrameClockView(this)

                        val clock = Constants.clockList[clockIndex]

                        when(clock.viewType) {
                            ClockViewType.TEXT_CLOCK.ordinal -> {
                                textClockView.visibility = View.VISIBLE
                                frameClockView.visibility = View.GONE
                                textClockView.setAttributes(clock)
                            }
                            ClockViewType.FRAME_CLOCK.ordinal -> {
                                textClockView.visibility = View.GONE
                                frameClockView.visibility = View.VISIBLE
                                frameClockView.setAttributes(clock)
                            }
                        }

                        textClockView.setHourTextSize(100f)
                        textClockView.setMinuteTextSize(100f)
                        textClockView.setOpacity(textTransparency)
                        frameClockView.setOpacity(frameTransparency)

                        clockColor?.let { it2 -> textClockView.setForegroundColor(it2) }
                        clockColor?.let { it2 -> frameClockView.setForegroundColor(it2) }


                        // SurfaceView
                        val overlayColor = SharedPrefRepository(this).getOverlayColor()
                        val alpha = SharedPrefRepository(this).getOverlayTransparency()
                        overlayIndex = SharedPrefRepository(this).getOverlayStyleIndex()

                        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        handler.postDelayed(checkMusicRunnable, 10)
//                        handler.postDelayed(visualizerRunnable, 1000)
                        byteArrays = generateRandomByteArray(128)


                        surfaceView = SurfaceView(this)
                        overlayView?.addView(surfaceView)


                        surfaceView?.setZOrderOnTop(true)
                        surfaceView?.holder?.setFormat(PixelFormat.TRANSLUCENT)

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

                        surfaceView.let {itView ->
                            if (itView != null) {
                                mVisualizerManager?.start(itView, Constants.visualizerList[overlayIndex])
                            }
                        }

                        val params = LinearLayout.LayoutParams(
                            300, 300
                        ).apply {
                            gravity = Gravity.CENTER
                        }

                        val params2 = ViewGroup.LayoutParams(
                            300, 300
                        )

                        textClockView.layoutParams = params
                        frameClockView.layoutParams = params2

                        overlayView?.addView(handlerView)
                        overlayView?.addView(textClockView)
                        overlayView?.addView(frameClockView)


                        wm!!.addView(overlayView, layoutParams)
                    }
                }
                "hide" -> {
                    if (overlayView != null) {
                        mVisualizerManager?.release()
                        mVisualizerManager = null
                        handler.removeCallbacks(checkMusicRunnable)

                        wm!!.removeView(overlayView)
                        overlayView = null
                        surfaceView = null
                    }
                }
                "stop" -> {
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    fun generateRandomByteArray(size: Int): ByteArray {
        val byteArray = ByteArray(size)
        Random.nextBytes(byteArray)
        return byteArray
    }

    private val checkMusicRunnable = object : Runnable {
        override fun run() {
            byteArrays = if (audioManager.isMusicActive) {
                generateRandomByteArray(128)
            } else {
                ByteArray(128)
            }
            handler.postDelayed(this, 10)
        }
    }

}