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


//<!-- Frame Attributes -->
//        <attr name="frameRadius" format="dimension"/>
//        <attr name="frameColor" format="color"/>
//        <attr name="frameThickness" format="dimension"/>
//
//        <!-- Seconds Hand Attributes -->
//        <attr name="showSecondsHand" format="boolean"/>
//        <attr name="secondHandColor" format="color"/>
//        <attr name="secondHandThickness" format="dimension"/>
//
//        <!-- Hour Hand Attributes -->
//        <attr name="hourHandColor" format="color"/>
//        <attr name="hourHandThickness" format="dimension"/>
//
//        <!-- Minute Hand Attributes -->
//        <attr name="minuteHandColor" format="color"/>
//        <attr name="minuteHandThickness" format="dimension"/>