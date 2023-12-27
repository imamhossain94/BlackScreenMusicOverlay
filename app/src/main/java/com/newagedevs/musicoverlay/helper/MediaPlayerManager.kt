package com.newagedevs.musicoverlay.helper

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.newagedevs.musicoverlay.R

class MediaPlayerManager(private val context: Context, private val resourceId: Int = R.raw.mixkit_just_kidding) {

    private var mediaPlayer: MediaPlayer? = null

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.setOnCompletionListener {
            Log.d("MediaPlayerManager", "Playback completed")
        }
    }

    fun play(): MediaPlayerManager {
        mediaPlayer?.start()
        return this
    }

    fun pause(): MediaPlayerManager {
        mediaPlayer?.pause()
        return this
    }

    fun stop(): MediaPlayerManager {
        mediaPlayer?.stop()
        return this
    }

    fun reset(): MediaPlayerManager {
        mediaPlayer?.seekTo(0)
        return this
    }

    fun getSessionId(): Int {
        return mediaPlayer?.audioSessionId ?: 0
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

