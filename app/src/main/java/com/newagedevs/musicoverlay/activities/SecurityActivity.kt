package com.newagedevs.musicoverlay.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivitySecurityBinding
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository
import com.newagedevs.musicoverlay.services.LockScreenUtil
import dev.oneuiproject.oneui.widget.Toast

class SecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityBinding

    private lateinit var lockScreenUtil: LockScreenUtil
    private lateinit var administratorPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lockScreenUtil = LockScreenUtil(this)

        binding.toolbarLayout.setNavigationButtonAsBack()

        binding.screenLockPrivacy.isChecked = SharedPrefRepository(this).isScreenLockPrivacyEnabled()
        binding.screenLockPrivacy.setOnCheckedChangeListener { _, isChecked ->

            if(!lockScreenUtil.active()) {
                lockScreenUtil.enableAdmin(administratorPermissionLauncher)
            } else {
                SharedPrefRepository(this).setScreenLockPrivacy(isChecked)
            }

            binding.screenLockPrivacy.isChecked = SharedPrefRepository(this).isScreenLockPrivacyEnabled()
        }

        administratorPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            if (!lockScreenUtil.active()) {
                Toast.makeText(this, "Device administrator permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }



}