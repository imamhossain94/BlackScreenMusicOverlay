package com.newagedevs.musicoverlay.receiver

interface AudioSessionListener {
    fun onAudioSessionOpened(sessionId: Int)
    fun onAudioSessionClosed()
}