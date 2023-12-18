package com.newagedevs.musicoverlay.activities

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.databinding.ActivityMainBinding
import com.newagedevs.musicoverlay.models.UnlockCondition
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.services.OverlayService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.startClockStyleActivity.setOnClickListener {
            startActivity(Intent(this, ClockStyleActivity::class.java))
        }

        binding.startOverlayStyleActivity.setOnClickListener {
            startActivity(Intent(this, OverlayStyleActivity::class.java))
        }

        binding.startHandlerStyleActivity.setOnClickListener {
            startActivity(Intent(this, HandlerStyleActivity::class.java))
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

        // Settings
        binding.toggleService.isChecked = SharedPrefRepository(this).isRunning()
        binding.toggleService.addOnSwitchChangeListener { _, isChecked ->
            SharedPrefRepository(this).setRunning(isChecked)
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

        askNotificationPermission()

        if (checkDrawOverlayPermission()) {
            startOverlayService()
        }

    }

    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        startForegroundService(intent)
    }

    private val REQUEST_CODE = 1
    private fun checkDrawOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /** check if we already  have permission to draw over other apps  */
            if (!Settings.canDrawOverlays(this)) {
                Log.d(TAG, "canDrawOverlays NOK")
                /** if not construct intent to request permission  */
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                /** request permission via start activity for result  */
                startActivityForResult(intent, REQUEST_CODE)
                return false
            } else {
                Log.d(TAG, "canDrawOverlays OK")
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /** check if received result code
         * is equal our requested code for draw permission   */
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService()
            }
        }
    }





    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Can post notifications.
        } else {
            // Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}