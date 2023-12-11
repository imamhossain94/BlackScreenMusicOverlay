package com.newagedevs.musicoverlay.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivitySecurityBinding
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository

class SecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.screenLockPrivacy.isChecked = SharedPrefRepository(this).isScreenLockPrivacyEnabled()
        binding.screenLockPrivacy.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefRepository(this).setScreenLockPrivacy(isChecked)
        }
    }

}