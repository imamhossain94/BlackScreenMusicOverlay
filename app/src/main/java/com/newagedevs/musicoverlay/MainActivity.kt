package com.newagedevs.musicoverlay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityMainBinding
import dev.oneuiproject.oneui.utils.ActivityUtils


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.aboutMusicOverlay.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

    }


}