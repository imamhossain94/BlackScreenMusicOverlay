package com.newagedevs.musicoverlay.models

import android.graphics.Color

//data class ClockModel(
//    val viewType: Int
//)

data class ClockModel(
    // View related properties
    val viewType: Int,
    val clockType: ClockType = ClockType.HOUR_12,
    val clockStyle: ClockStyle = ClockStyle.VERTICAL_NUMBER,

    // Update behavior
    val autoUpdate: Boolean = false,
    val showFrame: Boolean = false,
    val showSecondsHand: Boolean = false,

    // Time display properties
    val timePattern: String = "hh:mm",

    // Text color settings
    val hourColor: Int = Color.WHITE,
    val minuteColor: Int = Color.RED,
    val meridianColor: Int = Color.GRAY,

    // Text size settings
    val hourTextSize: Float = 26f,
    val minuteTextSize: Float = 26f,
    val meridianTextSize: Float = 26f,

    var frameRadius: Float = 40f,
    var frameColor: Int = Color.GRAY,
    var frameThickness: Float = 2f,
    var hourHandThickness: Float = 5f,
    var minuteHandThickness: Float = 5f,
    var secondHandThickness: Float = 3f,

)
