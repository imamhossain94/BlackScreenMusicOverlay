package com.newagedevs.musicoverlay.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.databinding.ActivityPermissionBinding
import com.newagedevs.musicoverlay.helper.NotificationUtil
import dev.oneuiproject.oneui.widget.Toast

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private lateinit var notificationUtil: NotificationUtil

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationUtil = NotificationUtil(this)

        if(isAllPermissionsAreGranted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.permissionRecordAudioCard.isEnabled = true
        binding.permissionAppearOnTopCard.isEnabled = true
//        binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()

        binding.startMainActivity.setOnClickListener {
            requestOverlayPermission()
            requestAllPermissions()
            if(isAllPermissionsAreGranted()){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

//        binding.permissionNotificationCard.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                notificationUtil.requestPermission(notificationPermissionLauncher)
//            }
//        }

//        binding.permissionRecordAudioCard.setOnClickListener {
//            requestAudioPermissions()
//        }

//        binding.permissionAppearOnTopCard.setOnClickListener {
//            requestOverlayPermission()
//        }

//        binding.permissionAdministratorCard.setOnClickListener {
//            lockScreenUtil.enableAdmin(administratorPermissionLauncher)
//        }

        notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
//            binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()

            if(!notificationUtil.isPermissionGranted()) {
                Toast.makeText(this, "Post notification permission not granted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.permissionNotificationCard.visibility = if(notificationUtil.isPermissionRequired()) View.VISIBLE else View.GONE

    }

    override fun onResume() {
        super.onResume()
        requestAllPermissions()
//        binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
    }


    private fun requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationUtil.requestPermission(notificationPermissionLauncher)
        }

        requestAudioPermissions()

//        if (!Settings.canDrawOverlays(this)) {
//            Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
//        }

        if(!notificationUtil.isPermissionGranted()) {
            Toast.makeText(this, "Post notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isAllPermissionsAreGranted(): Boolean {
        return                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED)
                && notificationUtil.isPermissionGranted()
    }

    private fun requestOverlayPermission() {
//        if (!Settings.canDrawOverlays(this)) {
//            val intent = Intent(
//                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:${this.packageName}")
//            )
//            overlayPermissionLauncher.launch(intent)
//        }
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
//            binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
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
//                    binding.startMainActivity.isEnabled = isAllPermissionsAreGranted()
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