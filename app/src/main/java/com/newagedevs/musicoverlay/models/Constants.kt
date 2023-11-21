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
                viewType = ClockViewType.FRAME_CLOCK.ordinal
            ),
            ClockModel(
                viewType = ClockViewType.FRAME_CLOCK.ordinal,
                autoUpdate = true,
                showSecondsHand = true
            ),
            ClockModel(
                viewType = ClockViewType.FRAME_CLOCK.ordinal,
                showFrame = true
            )
        )

        val visualizerList = arrayOf<Array<IRenderer>>(
            arrayOf(ColumnarType1Renderer()),
            arrayOf(ColumnarType2Renderer()),
            arrayOf(ColumnarType3Renderer()),
            arrayOf(LineRenderer(true)),
            arrayOf(CircleRenderer(true)),
            arrayOf(CircleRenderer(true), CircleBarRenderer(), LineRenderer(true))
        )
    }
}