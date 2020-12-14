package com.acruxcs.lawyer.ui.lawyersinfo

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.databinding.ItemTimeSelectionBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSelectionAdapter(private val interaction: Interaction? = null) :
    ListAdapter<LocalTime, TimeSelectionAdapter.TimeViewHolder>(DC()) {

    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TimeViewHolder(
        ItemTimeSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false).root,
        interaction
    )

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.itemView.isSelected = position == selected
        holder.bind(getItem(position))
    }

    fun swapData(data: List<LocalTime>) {
        submitList(data.toMutableList())
    }

    fun resetSelected() {
        selected = -1
    }

    inner class TimeViewHolder(
        itemView: View,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        private val binding = ItemTimeSelectionBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return
            val clicked = getItem(adapterPosition)
            if (adapterPosition == selected) {
                v?.isSelected = false
                selected = -1
            } else {
                selected = adapterPosition
                notifyDataSetChanged()
            }
            interaction?.onTimeSelected(clicked)
        }

        fun bind(item: LocalTime) = with(itemView) {
            with(binding) {
                textTime.text = item.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
        }
    }

    interface Interaction {
        fun onTimeSelected(time: LocalTime?)
    }

    private class DC : DiffUtil.ItemCallback<LocalTime>() {
        override fun areItemsTheSame(
            oldItem: LocalTime,
            newItem: LocalTime,
        ) = oldItem.hour == newItem.hour

        override fun areContentsTheSame(
            oldItem: LocalTime,
            newItem: LocalTime,
        ) = oldItem == newItem
    }
}
