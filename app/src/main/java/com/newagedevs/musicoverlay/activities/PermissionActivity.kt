package com.newagedevs.musicoverlay.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.newagedevs.musicoverlay.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private val audioPermissions = permissionsBuilder(Manifest.permission.RECORD_AUDIO).build()
    //        private val overlayPermissions = permissionsBuilder(Manifest.permission.SYSTEM_ALERT_WINDOW).build()
    private val allPermissions = permissionsBuilder(Manifest.permission.RECORD_AUDIO).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.permissionRecordAudioCard.isEnabled = true
        binding.permissionAppearOnTopCard.isEnabled = true
        binding.startMainActivity.isEnabled = false

        binding.startMainActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.permissionRecordAudioCard.setOnClickListener {
            audioPermissions.send {
                binding.startMainActivity.isEnabled = it.anyGranted()
            }
        }

        binding.permissionAppearOnTopCard.setOnClickListener {
            if (!Settings.canDrawOverlays(this@PermissionActivity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${this@PermissionActivity.packageName}")
                )
                this@PermissionActivity.startActivityForResult(intent, OVERLAY_REQUEST_CODE)
            }

            binding.startMainActivity.isEnabled = Settings.canDrawOverlays(this@PermissionActivity)

//            overlayPermissions.send {
//                binding.startMainActivity.isEnabled = allPermissions.checkStatus().allGranted()
//            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == OVERLAY_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this) && allPermissions.checkStatus().allGranted()) {
                binding.startMainActivity.isEnabled = true
            }
        }
    }

    companion object {
        const val OVERLAY_REQUEST_CODE = 1
    }

}