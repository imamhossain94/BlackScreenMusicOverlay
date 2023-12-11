package com.newagedevs.musicoverlay.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityGestureBinding
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository


class GestureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGestureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.gestureIncreaseVolume.isChecked = SharedPrefRepository(this).isGestureIncreaseVolumeEnabled()
        binding.gestureIncreaseVolume.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefRepository(this).setGestureIncreaseVolumeEnabled(isChecked)
        }

        binding.gestureDecreaseVolume.isChecked = SharedPrefRepository(this).isGestureDecreaseVolumeEnabled()
        binding.gestureDecreaseVolume.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefRepository(this).setGestureDecreaseVolumeEnabled(isChecked)
        }
    }

}