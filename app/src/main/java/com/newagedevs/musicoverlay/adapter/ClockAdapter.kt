package com.newagedevs.musicoverlay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockModel
import com.newagedevs.musicoverlay.models.ClockViewType

class ClockAdapter(private val clockList: List<ClockModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ClockViewType.TEXT_CLOCK.ordinal -> TextClockViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_clock, parent, false)
            )
            ClockViewType.FRAME_CLOCK.ordinal -> FrameClockViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_frame_clock, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextClockViewHolder -> {
                // Bind data for TextClockViewHolder
                // You can set listeners or other properties here
            }
            is FrameClockViewHolder -> {
                // Bind data for FrameClockViewHolder
                // You can set listeners or other properties here
            }
        }
    }

    override fun getItemCount(): Int = clockList.size

    override fun getItemViewType(position: Int): Int {
        return clockList[position].viewType
    }

    // ViewHolder classes for each clock type
    class TextClockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class FrameClockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
