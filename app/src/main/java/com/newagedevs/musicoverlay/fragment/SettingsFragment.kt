package com.newagedevs.musicoverlay.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.newagedevs.musicoverlay.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}