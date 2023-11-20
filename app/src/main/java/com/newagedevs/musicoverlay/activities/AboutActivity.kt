package com.newagedevs.musicoverlay.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater);
        setContentView(binding.root)

    }
}