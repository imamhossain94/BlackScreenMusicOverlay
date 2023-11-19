package com.newagedevs.musicoverlay

interface AudioSessionListener {
    fun onAudioSessionOpened(sessionId: Int)
    fun onAudioSessionClosed()
}