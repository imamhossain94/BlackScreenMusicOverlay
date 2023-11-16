package com.newagedevs.musicoverlay

import android.content.Context
import android.media.AudioManager
import android.os.Handler

interface AudioStatusListener {
    fun onAudioStatusChanged(isAudioActive: Boolean)
}

class AudioStatusChecker(private val context: Context, private val listener: AudioStatusListener) {
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val handler = Handler()

    private val checkAudioStatusRunnable = object : Runnable {
        override fun run() {
            checkAudioStatus()
            handler.postDelayed(this, 2000) // Check every 2 seconds
        }
    }

    fun startChecking() {
        handler.postDelayed(checkAudioStatusRunnable, 0) // Start checking immediately
    }

    fun stopChecking() {
        handler.removeCallbacks(checkAudioStatusRunnable)
    }

    private fun checkAudioStatus() {
        val isMediaPlaying = audioManager.isMusicActive
        // Notify the listener about the audio status change
        listener.onAudioStatusChanged(isMediaPlaying)
    }
}
