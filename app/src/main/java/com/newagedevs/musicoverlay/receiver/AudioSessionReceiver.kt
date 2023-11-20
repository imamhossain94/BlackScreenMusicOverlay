package com.newagedevs.musicoverlay.receiver

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.util.Log
import com.newagedevs.musicoverlay.activities.OverlayStyleActivity

class AudioSessionReceiver : BroadcastReceiver() {

    private lateinit var listener: AudioSessionListener

    override fun onReceive(context: Context?, intent: Intent) {

        listener = context as OverlayStyleActivity

        val audioSession: Int = intent.getIntExtra(
            AudioEffect.EXTRA_AUDIO_SESSION,
            AudioEffect.ERROR_BAD_VALUE)

        //val packageName: String? = intent.getStringExtra(Equalizer.EXTRA_PACKAGE_NAME)

        Log.w(TAG, "Invalid or missing audio session 2 $audioSession")

        // check audio session
        if ((audioSession == AudioEffect.ERROR_BAD_VALUE) || (audioSession < 0)) {
            Log.w(TAG, "Invalid or missing audio session $audioSession")
            return;
        }
        // open audio session
        if (intent.action.equals(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)) {
            listener.onAudioSessionOpened(audioSession)
        }
        // close audio session
        if (intent.action.equals(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)) {
            listener.onAudioSessionClosed()
        }
    }
}