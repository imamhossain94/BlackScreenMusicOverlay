package com.newagedevs.musicoverlay.activities

import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.newagedevs.musicoverlay.databinding.ActivityAboutBinding
import dev.oneuiproject.oneui.layout.AppInfoLayout
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.newagedevs.musicoverlay.models.Constants

class AboutActivity : AppCompatActivity(), AppInfoLayout.OnClickListener {

    private lateinit var binding: ActivityAboutBinding

    private var appInfoLayout: AppInfoLayout? = null
    private var appUpdateManager: AppUpdateManager? = null
    private var appUpdateInfo: AppUpdateInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater);
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appInfoLayout = binding.appInfoLayout
        checkForUpdate()

        appInfoLayout?.setMainButtonClickListener(this)

        binding.aboutOtherApp.setOnClickListener { _: View? ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.publisherName)))
        }
        binding.aboutSourceCode.setOnClickListener { _: View? ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.sourceCodeUrl)))
        }
        binding.aboutPrivacyPolicy.setOnClickListener { _: View? ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.privacyPolicyUrl)))
        }

    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
    private fun checkForUpdate() {
        appInfoLayout!!.status = AppInfoLayout.LOADING
        val networkInfo = (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (!(networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected)) {
            appInfoLayout!!.status = AppInfoLayout.NO_CONNECTION
            return
        }
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appInfoLayout!!.status = AppInfoLayout.UPDATE_AVAILABLE
                this.appUpdateInfo = appUpdateInfo
            } else {
                appInfoLayout!!.status = AppInfoLayout.NO_UPDATE
            }
        }?.addOnFailureListener { e ->
            e.printStackTrace()
            appInfoLayout!!.status = AppInfoLayout.NOT_UPDATEABLE
        }
    }

    @Suppress("DEPRECATION")
    override fun onUpdateClicked(v: View) {
        if (appUpdateInfo == null){
            return
        }
        else{
            try {
                appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo!!,
                    AppUpdateType.IMMEDIATE,
                    this,
                    6000
                )
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRetryClicked(v: View) {
        checkForUpdate()
    }
}