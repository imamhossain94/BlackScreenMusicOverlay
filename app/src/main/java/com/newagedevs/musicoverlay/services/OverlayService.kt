package com.newagedevs.musicoverlay.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.newagedevs.musicoverlay.R


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
//


class OverlayService : Service(), OnTouchListener, View.OnClickListener {
    private var wm: WindowManager? = null
    private var button: Button? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onCreate()
        val CHANNEL_ID = "channel1"

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
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_show, "Show", getPendingIntent("show"))
            .addAction(R.drawable.ic_hide, "Hide", getPendingIntent("hide"))
            .addAction(R.drawable.ic_stop, "Stop", getPendingIntent("stop"))
            .build()


        startForeground(1, notification)



        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        button = Button(this)
        button!!.setBackgroundResource(R.drawable.ic_show)
        button!!.text = "Button"
        button!!.alpha = 1f
        button!!.setBackgroundColor(Color.BLUE)
        button!!.setOnClickListener(this)

        val type =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.START or Gravity.TOP
        params.x = 0
        params.y = 0
        wm!!.addView(button, params)
        return START_NOT_STICKY
    }


    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, OverlayService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        Log.d(TAG, " ++++ On touch")
        return false
    }

    override fun onClick(v: View?) {
        Log.d(TAG, " ++++ On click")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (button != null) {
            wm!!.removeView(button)
            button = null
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "OverlayService"
    }
}