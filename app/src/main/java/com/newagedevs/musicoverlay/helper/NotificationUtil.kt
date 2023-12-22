package com.newagedevs.musicoverlay.helper

import android.Manifest
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.newagedevs.musicoverlay.services.DeviceAdmin

class NotificationUtil(context: Context) {

    val isPermissionRequired: () -> Boolean = {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
    }

    val isPermissionGranted: () -> Boolean = {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(launcher: ActivityResultLauncher<String>) {
        if (isPermissionRequired() && !isPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


}