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
        val isRunning = SharedPrefRepository(this).isRunning()

        if (isRunning) {
            OverlayService.start(this)
        }

        binding.toggleService.isChecked = isRunning
        binding.toggleService.addOnSwitchChangeListener { _, isChecked ->
            SharedPrefRepository(this).setRunning(isChecked)
            if (isChecked) {
                OverlayService.start(this)
            }else {
                OverlayService.stop(this)
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
    }

}