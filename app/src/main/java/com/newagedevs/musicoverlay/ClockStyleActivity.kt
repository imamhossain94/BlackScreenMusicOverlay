package com.newagedevs.musicoverlay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityClockStyleBinding

class ClockStyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClockStyleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClockStyleBinding.inflate(layoutInflater);
        setContentView(binding.root)
    }
}