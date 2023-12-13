package com.newagedevs.musicoverlay.fragment

import android.content.Context
import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        initPreferences()
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference!!.key) {
            "handlerPosition" -> {
                mContext?.let { SharedPrefRepository(it).setHandlerPosition(newValue as String) }
                return true
            }
            "lockHandler" -> {
                mContext?.let { SharedPrefRepository(it).setLockHandlerPositionEnabled(newValue as Boolean) }
                return true
            }
        }

        return false
    }

    private fun initPreferences() {
        val handlerPosition: DropDownPreference? = findPreference("handlerPosition")
        handlerPosition?.onPreferenceChangeListener = this
        val lockHandler: SwitchPreferenceCompat? = findPreference("lockHandler")
        lockHandler?.onPreferenceChangeListener = this
    }

}
