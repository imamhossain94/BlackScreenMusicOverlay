package com.newagedevs.musicoverlay.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.databinding.ActivityPermissionBinding
import dev.oneuiproject.oneui.widget.Toast


class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(isAllPermissionsAreGranted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.permissionRecordAudioCard.isEnabled = true
        binding.permissionAppearOnTopCard.isEnabled = true
        binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()

        binding.startMainActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.permissionRecordAudioCard.setOnClickListener {
            requestAudioPermissions()
        }

        binding.permissionAppearOnTopCard.setOnClickListener {
            requestOverlayPermission()

        }

        overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
            } else {
                Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
    }


    private fun isAllPermissionsAreGranted(): Boolean {
        return Settings.canDrawOverlays(this) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${this.packageName}")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
                } else {
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    companion object {
        const val RECORD_AUDIO_REQUEST_CODE = 1
    }
}