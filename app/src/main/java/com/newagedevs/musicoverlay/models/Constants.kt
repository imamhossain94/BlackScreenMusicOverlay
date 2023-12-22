package com.newagedevs.musicoverlay.models

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import com.newagedevs.musicoverlay.R
import io.github.jeffshee.visualizer.painters.Painter
import io.github.jeffshee.visualizer.painters.fft.FftBar
import io.github.jeffshee.visualizer.painters.fft.FftCBar
import io.github.jeffshee.visualizer.painters.fft.FftCLine
import io.github.jeffshee.visualizer.painters.fft.FftCWave
import io.github.jeffshee.visualizer.painters.fft.FftCWaveRgb
import io.github.jeffshee.visualizer.painters.fft.FftLine
import io.github.jeffshee.visualizer.painters.fft.FftWave
import io.github.jeffshee.visualizer.painters.fft.FftWaveRgb
import io.github.jeffshee.visualizer.painters.misc.Gradient
import io.github.jeffshee.visualizer.painters.misc.Icon
import io.github.jeffshee.visualizer.painters.modifier.Beat
import io.github.jeffshee.visualizer.painters.modifier.Blend
import io.github.jeffshee.visualizer.painters.modifier.Compose
import io.github.jeffshee.visualizer.painters.modifier.Glitch
import io.github.jeffshee.visualizer.painters.modifier.Move
import io.github.jeffshee.visualizer.painters.modifier.Shake
import io.github.jeffshee.visualizer.painters.waveform.WfmAnalog
import io.github.jeffshee.visualizer.utils.Preset

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




        fun visualizerList(context: Context, topMargin:Float = 0.5f, bottomMargin:Float = 0.5f): List<Painter?> {
            //val background = BitmapFactory.decodeResource(context.resources, R.drawable.background)
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.speaker)
            val circleBitmap = Icon.getCircledBitmap(bitmap)

            return listOf(
                null,
                // Basic components
                Compose(WfmAnalog()),
                Compose(Move(FftBar(), yR = bottomMargin)),
                Compose(Move(FftLine(), yR = bottomMargin)),
                Compose(Move(FftWave(), yR = bottomMargin)),
                Compose(Move(FftWaveRgb(), yR = bottomMargin)),
                Compose(Move(FftBar(side = "b"), yR = topMargin)),
                Compose(Move(FftLine(side = "b"), yR = topMargin)),
                Compose(Move(FftWave(side = "b"), yR = topMargin)),
                Compose(Move(FftWaveRgb(side = "b"), yR = topMargin)),
                Compose(FftBar(side = "ab")),
                Compose(FftLine(side = "ab")),
                Compose(FftWave(side = "ab")),
                Compose(FftWaveRgb(side = "ab")),

                // Basic components (Circle)
                Compose(FftCLine()),
                Compose(FftCWave()),
                Compose(FftCWaveRgb()),
                Compose(FftCLine(side = "b")),
                Compose(FftCWave(side = "b")),
                Compose(FftCWaveRgb(side = "b")),
                Compose(FftCLine(side = "ab")),
                Compose(FftCWave(side = "ab")),
                Compose(FftCWaveRgb(side = "ab")),

                //Blend
                Blend(
                    FftCLine().apply { paint.strokeWidth = 8f;paint.strokeCap = Paint.Cap.ROUND },
                    Gradient(preset = Gradient.RADIAL)
                ),
                Blend(
                    FftCBar(side = "b", gapX = 8f).apply { paint.style = Paint.Style.FILL },
                    Gradient(preset = Gradient.SWEEP, hsv = true)
                ),
                Blend(
                    FftCBar(side = "ab", gapX = 8f).apply { paint.style = Paint.Style.FILL },
                    Gradient(preset = Gradient.SWEEP, hsv = true)
                ),
                Move(Blend(
                        FftLine().apply { paint.strokeWidth = 8f;paint.strokeCap = Paint.Cap.ROUND },
                        Gradient(preset = Gradient.LINEAR_HORIZONTAL)
                    ), yR = bottomMargin),
                Move(Blend(
                        FftLine().apply { paint.strokeWidth = 8f;paint.strokeCap = Paint.Cap.ROUND },
                        Gradient(preset = Gradient.LINEAR_VERTICAL, hsv = true)
                    ), yR = bottomMargin),
                Move(Blend(
                        FftLine().apply { paint.strokeWidth = 8f;paint.strokeCap = Paint.Cap.ROUND },
                        Gradient(preset = Gradient.LINEAR_VERTICAL_MIRROR, hsv = true)
                    ), yR = bottomMargin),

                // Composition
                Glitch(Beat(Preset.getPresetWithBitmap("cIcon", circleBitmap))),
                Compose(
                    WfmAnalog().apply { paint.alpha = 150 },
                    Shake(Preset.getPresetWithBitmap("cWaveRgbIcon", circleBitmap)).apply {
                        animX.duration = 1000
                        animY.duration = 2000
                    }
                ),
                //Compose(Preset.getPresetWithBitmap("liveBg", background), FftCLine().apply { paint.strokeWidth = 8f;paint.strokeCap = Paint.Cap.ROUND })
            )
        }

    }

}