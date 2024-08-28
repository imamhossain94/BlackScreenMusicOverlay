package com.newagedevs.musicoverlay.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.musicoverlay.R
import com.newagedevs.musicoverlay.models.ClockModel
import com.newagedevs.musicoverlay.models.ClockViewType
import com.newagedevs.musicoverlay.models.Constants
import com.newagedevs.musicoverlay.view.FrameClockView
import com.newagedevs.musicoverlay.view.TextClockView

class ClockAdapter(
    private var clockList: List<ClockModel>,
    private val listener: OnClockItemClickListener,
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClockItemClickListener {
        fun onTextClockClick(position: Int)
        fun onFrameClockClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = when (viewType) {
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

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                handleItemClick(position)
            }
        }

        return viewHolder
    }

    override fun getItemCount(): Int = clockList.size

    override fun getItemViewType(position: Int): Int {
        return clockList[position].viewType
    }

    class TextClockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class FrameClockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextClockViewHolder -> {
                bindTextClockViewHolder(holder, position)
                updateItemView(holder.itemView, position)
            }
            is FrameClockViewHolder -> {
                bindFrameClockViewHolder(holder, position)
                updateItemView(holder.itemView, position)
            }
        }
    }

    private fun handleItemClick(position: Int) {
        if (selectedItemPosition != position) {
            val previousSelectedItemPosition = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelectedItemPosition)
            notifyItemChanged(selectedItemPosition)

            when (clockList[position].viewType) {
                ClockViewType.TEXT_CLOCK.ordinal -> listener.onTextClockClick(position)
                ClockViewType.FRAME_CLOCK.ordinal -> listener.onFrameClockClick(position)
            }
        }
    }

    private fun bindTextClockViewHolder(holder: TextClockViewHolder, position: Int) {
        val clockModel = clockList[position]

        // Example: Set properties of TextClockViewHolder based on ClockModel
        val view = holder.itemView.findViewById<TextClockView>(R.id.textClockView)

        view.setClockStyle(clockModel.clockStyle)
        view.setClockType(clockModel.clockType)
        view.setMinuteTextColor(clockModel.minuteColor)

//        val proStatus = holder.itemView.findViewById<ImageView>(R.id.pro_status)
//        proStatus.visibility = if(clockModel.isPro) View.VISIBLE else View.INVISIBLE
    }

    private fun bindFrameClockViewHolder(holder: FrameClockViewHolder, position: Int) {
        val clockModel = clockList[position]

        // Example: Set properties of FrameClockViewHolder based on ClockModel
        val view = holder.itemView.findViewById<FrameClockView>(R.id.frameClockView)

        view.setAutoUpdate(clockModel.autoUpdate)
        view.showFrame(clockModel.showFrame)
        view.showSecondsHand(clockModel.showSecondsHand)
        view.setFrameRadius(clockModel.frameRadius)
        view.setMinuteHandColor(clockModel.minuteHandColor)

//        val proStatus = holder.itemView.findViewById<ImageView>(R.id.pro_status)
//        proStatus.visibility = if(clockModel.isPro) View.VISIBLE else View.INVISIBLE
    }

    private fun updateItemView(itemView: View, position: Int) {
        if (selectedItemPosition == position) {
            // Apply border or background color to highlight the selected item
            itemView.setBackgroundResource(R.drawable.selected_clock_item_background)
        } else {
            // Reset the view appearance
            itemView.setBackgroundResource(R.drawable.clock_item_background)
        }
    }

    // Method to update the clockList and refresh the RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun updateClockList(value: List<ClockModel>) {
        clockList = value
//        selectedItemPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}

