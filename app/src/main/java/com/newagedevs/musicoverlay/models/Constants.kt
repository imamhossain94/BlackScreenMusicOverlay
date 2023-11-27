package com.newagedevs.musicoverlay.models

import android.graphics.Color
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
                showSecondsHand = true
            )
        )

        val visualizerList = arrayOf<Array<IRenderer>>(
            emptyArray(),
            arrayOf(ColumnarType1Renderer()),
            arrayOf(ColumnarType2Renderer()),
            arrayOf(ColumnarType3Renderer()),
            arrayOf(LineRenderer(true)),
            arrayOf(CircleRenderer(true)),
            arrayOf(CircleRenderer(true), CircleBarRenderer(), LineRenderer(true))
        )
    }
}