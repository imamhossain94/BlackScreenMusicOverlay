package com.newagedevs.musicoverlay.activities

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import com.newagedevs.musicoverlay.databinding.ActivityMainBinding
import com.newagedevs.musicoverlay.models.UnlockCondition
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.services.OverlayService
import com.newagedevs.musicoverlay.services.OverlayServiceInterface


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var overlayServiceInterface: OverlayServiceInterface? = null

    private var isBound = false
    private var isRunning = false


    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            overlayServiceInterface = (iBinder as OverlayService.LocalBinder).instance()
            isBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            overlayServiceInterface = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarLayout.setNavigationButtonAsBack()

        isRunning = SharedPrefRepository(this).isRunning()

        if (isRunning) {
            val serviceIntent = Intent(this, OverlayService::class.java)
            if(!isServiceRunning(OverlayService::class.java)) {
                startForegroundService(serviceIntent)
            }
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.startClockStyleActivity.setOnClickListener {
            startActivity(Intent(this, ClockStyleActivity::class.java))
        }

        binding.startOverlayStyleActivity.setOnClickListener {
            startActivity(Intent(this, OverlayStyleActivity::class.java))
        }

        binding.startHandlerStyleActivity.setOnClickListener {
            startActivity(Intent(this, HandlerStyleActivity::class.java))
            if (isBound) {
                isRunning = SharedPrefRepository(this).isRunning()
                if (isRunning) {
                    overlayServiceInterface?.hide()
                }
            }
        }

        binding.startSecurityActivity.setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }

        binding.startGestureControlActivity.setOnClickListener {
            startActivity(Intent(this, GestureActivity::class.java))
        }

        binding.aboutMusicOverlay.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.toggleService.isChecked = isRunning
        binding.toggleService.addOnSwitchChangeListener { _, isChecked ->
            SharedPrefRepository(this).setRunning(isChecked)
            isRunning = isChecked

            if (isChecked && !isBound) {
                val serviceIntent = Intent(this, OverlayService::class.java)
                if(!isServiceRunning(OverlayService::class.java)){
                    startForegroundService(serviceIntent)
                }
                bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
                overlayServiceInterface?.show()
                isBound = true
            } else if (!isChecked && isBound) {
                unbindService(connection)
                isBound = false
            }
        }

        when (SharedPrefRepository(this).getUnlockCondition()) {
            UnlockCondition.TAP.displayText -> binding.tapToUnlock.isChecked = true
            UnlockCondition.DOUBLE_TAP.displayText -> binding.doubleTapToUnlock.isChecked = true
            UnlockCondition.LONG_PRESS.displayText -> binding.longPressToUnlock.isChecked = true
        }

        binding.unlockCondition.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<AppCompatRadioButton>(checkedId)
            val selectedUnlockCondition = UnlockCondition.values().firstOrNull { it.displayText == radioButton.text.toString().trim() }
            selectedUnlockCondition?.let {
                SharedPrefRepository(this).setUnlockCondition(it.displayText)
            }
        }

        binding.alwaysOnDisplay.isChecked = SharedPrefRepository(this).isAlwaysOnDisplay()
        binding.startClockStyleActivity.isEnabled = SharedPrefRepository(this).isAlwaysOnDisplay()

        binding.alwaysOnDisplay.setOnCheckedChangeListener { _, isChecked ->
            binding.startClockStyleActivity.isEnabled = isChecked
            SharedPrefRepository(this).setAlwaysOnDisplay(isChecked)
        }

        binding.otherAppGestureVolume.setOnClickListener {
            val packageName = "com.newagedevs.gesturevolume"
            val className = "com.newagedevs.gesturevolume.view.ui.main.MainActivity"
            try{
                val intent = Intent("android.intent.action.MAIN")
                intent.setClassName(packageName, className)
                startActivity(intent)
            }catch(_:Exception) {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
                startActivity(playStoreIntent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val isRunning = SharedPrefRepository(this).isRunning()
        binding.toggleService.isChecked = isRunning
        if (isBound) {
            if (isRunning) {
                overlayServiceInterface?.show()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}