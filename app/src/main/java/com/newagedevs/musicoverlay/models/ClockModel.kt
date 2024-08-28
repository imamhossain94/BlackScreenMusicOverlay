package com.newagedevs.musicoverlay.models

import android.graphics.Color

//data class ClockModel(
//    val viewType: Int
//)

data class ClockModel(
    val isPro: Boolean = false,

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
    var minuteColor: Int = Color.RED,
    val meridianColor: Int = Color.GRAY,

    // Text size settings
    val hourTextSize: Float = 26f,
    val minuteTextSize: Float = 26f,
    val meridianTextSize: Float = 26f,

    // Frame color settings
    var frameColor: Int = Color.GRAY,
    val hourHandColor: Int = Color.WHITE,
    var minuteHandColor: Int = Color.RED,
    val secondHandColor: Int = Color.GRAY,

    var frameRadius: Float = 40f,
    var frameThickness: Float = 2f,
    var hourHandThickness: Float = 5f,
    var minuteHandThickness: Float = 5f,
    var secondHandThickness: Float = 3f,

)
