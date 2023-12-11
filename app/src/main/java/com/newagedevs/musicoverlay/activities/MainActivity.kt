package com.newagedevs.musicoverlay.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import com.newagedevs.musicoverlay.databinding.ActivityMainBinding
import com.newagedevs.musicoverlay.models.UnlockCondition
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.startOverlayStyleActivity.setOnClickListener {
            startActivity(Intent(this, OverlayStyleActivity::class.java))
        }

        binding.startClockStyleActivity.setOnClickListener {
            startActivity(Intent(this, ClockStyleActivity::class.java))
        }

        binding.startGestureControlActivity.setOnClickListener {
            startActivity(Intent(this, GestureActivity::class.java))
        }

        binding.startSecurityActivity.setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
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

    }


}