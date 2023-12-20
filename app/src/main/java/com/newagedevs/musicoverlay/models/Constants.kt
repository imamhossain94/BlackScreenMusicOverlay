package com.newagedevs.musicoverlay.models

import android.graphics.Color
import android.graphics.Paint
import me.bogerchan.niervisualizer.renderer.IRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleRenderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType1Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType2Renderer
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType3Renderer
import me.bogerchan.niervisualizer.renderer.line.LineRenderer

class Constants {

    companion object {
        val clockList = mutableListOf(
            ClockModel(
                viewType = ClockViewType.TEXT_CLOCK.ordinal,
                clockStyle = ClockStyle.VERTICAL_NUMBER
            ),
            ClockModel(
                viewType = ClockViewType.TEXT_CLOCK.ordinal,
                clockStyle = ClockStyle.HORIZONTAL_NUMBER
            ),
            ClockModel(
                viewType = ClockViewType.TEXT_CLOCK.ordinal,
                clockStyle = ClockStyle.HORIZONTAL_NUMBER_2
            ),

            // Frame Clocks
            ClockModel(
                viewType = ClockViewType.FRAME_CLOCK.ordinal,
                autoUpdate = false,
                showFrame = false,
                showSecondsHand = false
            ),
            ClockModel(
                viewType = ClockViewType.FRAME_CLOCK.ordinal,
                autoUpdate = false,
                showFrame = false,
                showSecondsHand = true
            ),
            ClockModel(
                viewType = ClockViewType.FRAME_CLOCK.ordinal,
                autoUpdate = false,
                showFrame = true,
                showSecondsHand = true,
                frameRadius = 1000f
            )
        )

        val visualizerList = arrayOf<Array<IRenderer>>(
            arrayOf(ColumnarType1Renderer(
                paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#00000000")
                },
            )),
            arrayOf(ColumnarType1Renderer()),
            arrayOf(ColumnarType2Renderer()),
            arrayOf(ColumnarType3Renderer()),
            arrayOf(LineRenderer(true)),
            arrayOf(CircleRenderer(true)),
            arrayOf(CircleRenderer(true), CircleBarRenderer(), LineRenderer(true))
        )

        val feedbackMail = arrayOf("imamagun94@gmail.com")
        val contactMail = arrayOf("imamagun94@gmail.com")
        const val privacyPolicyUrl = "https://newagedevs-privacy-policy.blogspot.com/2023/05/gesture-volume.html"
        const val sourceCodeUrl = "https://github.com/imamhossain94/BlackScreenMusicOverlay"
        const val publisherName = "https://play.google.com/store/apps/developer?id=NewAgeDevs"
        const val appStoreId = "https://play.google.com/store/apps/details?id=com.newagedevs.musicoverlay"

        const val showAdsOnEveryClick: Int = 5
        const val showAdsOnEveryOpen: Int = 3

        // Default values for clock and overlay
        const val defaultClockColor: Int = Color.WHITE
        const val defaultTextClockTransparency: Float = 100f
        const val defaultFrameClockTransparency: Int = 255
        const val defaultOverlayColor: Int = Color.BLACK
        const val defaultOverlayTransparency: Int = 255

        // Default unlock condition
        var defaultUnlockCondition: String = UnlockCondition.TAP.displayText

        // Default values for handler
        const val defaultHandlerPosition: String = "Right"
        const val defaultHandlerColor: Int = Color.WHITE
        const val defaultHandlerTransparency: Int = 255
        const val defaultHandlerSize: Int = 100
        const val defaultHandlerWidth: Int = 0

        val handlerWidthList = arrayOf(40, 50, 60)
    }

}