package com.newagedevs.musicoverlay.services

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class LockScreenUtil(context: Context) {

    private var devicePolicyManager = context.getSystemService(
        Activity.DEVICE_POLICY_SERVICE
    ) as DevicePolicyManager

    private var componentName: ComponentName = ComponentName(context, DeviceAdmin::class.java)

    val active: () -> Boolean = { devicePolicyManager.isAdminActive(componentName) }

    fun lockScreen() {
        if (active()) {
            devicePolicyManager.lockNow()
        }
    }

    fun enableAdmin(launcher: ActivityResultLauncher<Intent>) {
        if (!active()) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Screen lock requires administrator permissions."
                )
            }
            launcher.launch(intent)
        }
    }

    fun disableAdmin() {
        devicePolicyManager.removeActiveAdmin(componentName)
    }

}