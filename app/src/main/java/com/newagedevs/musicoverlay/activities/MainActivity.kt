package com.newagedevs.musicoverlay.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityMainBinding


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

    }


}