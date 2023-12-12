package com.newagedevs.musicoverlay.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.databinding.ActivityHandlerStyleBinding
import com.newagedevs.musicoverlay.fragment.SettingsFragment
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository

class HandlerStyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHandlerStyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHandlerStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.settings_fragment, SettingsFragment())
//                .commit()
//        }

    }

}