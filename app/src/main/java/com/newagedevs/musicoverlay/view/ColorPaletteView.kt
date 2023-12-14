package com.newagedevs.musicoverlay.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.picker.app.SeslColorPickerDialog
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.preferences.SharedPrefRepository


class ColorPaletteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),  SeslColorPickerDialog.OnColorSetListener{


    interface ColorSelectionListener {
        fun onColorSelected(color: Int)
    }

    private var currentColor: Int = R.color.paletteColor9
    private var recentColors: ArrayList<Int> = ArrayList()

    private val colorArray: IntArray = intArrayOf(
        R.color.paletteColor1, R.color.paletteColor2, R.color.paletteColor3,
        R.color.paletteColor4, R.color.paletteColor5, R.color.paletteColor6,
        R.color.paletteColor7, R.color.paletteColor8, R.color.paletteColor9,
        R.color.paletteColor10, R.color.paletteColor11, R.color.paletteColor12
    ).map { ContextCompat.getColor(context, it) }.toIntArray()

    private var colorSelectionListener: ColorSelectionListener? = null

    init {
        orientation = VERTICAL
        setPadding(1.dpToPx(), 5.dpToPx(), 1.dpToPx(), 1.dpToPx())
        setupColorPalette()
        recentColors.add(currentColor)
    }

    private fun Int.dpToPx(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    fun setColorSelectionListener(listener: ColorSelectionListener) {
        colorSelectionListener = listener
    }

    fun setDefaultSelectedColor(color: Int) {
        currentColor = color
        for (rowIndex in 0 until childCount) {
            val rowLayout = getChildAt(rowIndex) as LinearLayout
            for (colIndex in 0 until rowLayout.childCount) {
                val cell = rowLayout.getChildAt(colIndex) as? TextView
                if (cell?.background is GradientDrawable) {
                    val cellColor = (cell.background as GradientDrawable).color?.defaultColor ?: Color.TRANSPARENT
                    if (cellColor == color) {
                        handleColorSelection(cell, color)
                        return
                    }else {
                        if(rowIndex == 1 && colIndex == 10) cell.background = createCircleDrawableBorderGradient()
                    }
                }
            }
        }

    }

    private fun setupColorPalette() {
        for (row in 0 until 2) {
            val rowLayout = createRowLayout()
            addView(rowLayout)

            for (col in 0 until 11) {
                if (col % 2 != 0) {
                    rowLayout.addView(View(context), createLayoutParams(1f))
                } else if (row == 1 && col == 10) {
                    rowLayout.addView(createGradientColorCell())
                } else {
                    val color = colorArray[row * 6 + (col / 2)]
                    val colorCell = createColorCell(color)
                    rowLayout.addView(colorCell)
                }
            }
        }
    }

    private fun createRowLayout(): LinearLayout {
        val rowLayout = LinearLayout(context)
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rowLayout.layoutParams = layoutParams
        rowLayout.orientation = HORIZONTAL
        return rowLayout
    }

    private fun createLayoutParams(weight: Float): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0,
            weight
        )
    }

    private fun createColorCell(color: Int): TextView {
        val colorCell = createTextViewWithLayout(36.dpToPx(), 36.dpToPx(), 8, 8, 8, 60)
        colorCell.background = createCircleDrawable(color)
        colorCell.setOnClickListener { handleColorSelection(colorCell, color) }
        return colorCell
    }

    private fun createGradientColorCell(): TextView {
        val colorCell = createTextViewWithLayout(36.dpToPx(), 36.dpToPx(), 8, 8, 8, 60)
        colorCell.background = createCircleDrawableGradient(context)
        colorCell.setOnClickListener { handleGradientColorSelection(colorCell) }
        return colorCell
    }

    private fun createTextViewWithLayout(width: Int, height: Int, marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int): TextView {
        val colorCell = TextView(context)
        val layoutParams = LayoutParams(width, height)
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        colorCell.layoutParams = layoutParams
        return colorCell
    }

    private fun handleColorSelection(colorCell: TextView, color: Int) {
        resetColorCells()
        colorCell.isSelected = true
        colorCell.background = createCircleDrawableBorder(color)

        colorSelectionListener?.onColorSelected(color)
    }

    private fun handleGradientColorSelection(colorCell: TextView) {
        resetColorCells()
        colorCell.isSelected = true
        colorCell.background = createCircleDrawableBorderGradient()

        openColorPickerDialog()
    }

    private fun resetColorCells() {
        for (rowIndex in 0 until childCount) {
            val rowLayout = getChildAt(rowIndex) as LinearLayout
            for (colIndex in 0 until rowLayout.childCount) {
                val cell = rowLayout.getChildAt(colIndex) as? TextView
                cell?.background = createCircleDrawable(colorArray[rowIndex * 6 + (colIndex / 2)])
                cell?.isSelected = false
                if(rowIndex == 1 && colIndex == 10) cell?.background = createCircleDrawableGradient(context)
            }
        }
    }

    private fun createCircleDrawable(color: Int): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setColor(color)
        shape.setStroke(1, Color.GRAY)
        return shape
    }

    private fun createCircleDrawableBorder(color: Int, borderWidth: Int = 5, borderColor: Int = Color.BLUE): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setColor(color)
        shape.setStroke(borderWidth, borderColor)
        return shape
    }

    private fun createCircleDrawableGradient(context: Context): Drawable {
        val shape = GradientDrawable()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            shape.innerRadiusRatio = 0.15f
            shape.thicknessRatio = 0.075f
        }
        shape.shape = GradientDrawable.OVAL
        val color1 = ContextCompat.getColor(context, R.color.paletteColor3)
        val color2 = ContextCompat.getColor(context, R.color.paletteColor11)
        val color3 = ContextCompat.getColor(context, R.color.paletteColor2)
        shape.colors = intArrayOf(color1, color2, color3)
        shape.setGradientCenter(0.5f, 0.5f)
        shape.gradientType = GradientDrawable.SWEEP_GRADIENT
        return shape
    }

    private fun createCircleDrawableBorderGradient(borderWidth: Int = 5, borderColor: Int = Color.BLUE): Drawable {
        val shape = createCircleDrawableGradient(context) as GradientDrawable
        shape.setStroke(borderWidth, borderColor)
        return shape
    }


    private fun openColorPickerDialog() {
        recentColors = SharedPrefRepository(context).getRecentColors()
        val dialog = SeslColorPickerDialog(context, this, currentColor, buildIntArray(recentColors))
        dialog.setTransparencyControlEnabled(false)
        dialog.show()
    }


    override fun onColorSet(color: Int) {
        colorSelectionListener?.onColorSelected(color)
        currentColor = color

        if (recentColors.size == 6) {
            recentColors.remove(5)
        }
        recentColors.add(0, color)

        SharedPrefRepository(context).setRecentColors(recentColors)
    }

    private fun buildIntArray(integers: ArrayList<Int>): IntArray {
        val ints = IntArray(integers.size)
        var i = 0
        for (n in integers) {
            ints[i++] = n
        }
        return ints
    }

}




