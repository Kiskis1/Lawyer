package com.acruxcs.lawyer.ui.lawyersinfo

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Case

class LawyersCaseAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Case, LawyersCaseAdapter.LawyersCaseViewHolder>(CaseDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyersCaseViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_case, parent, false), interaction
    )

    override fun onBindViewHolder(holder: LawyersCaseViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Case>) {
        submitList(data.toMutableList())
    }

    inner class LawyersCaseViewHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
        }

        fun bind(item: Case) = with(itemView) {
            // TODO: Bind the data with View
        }
    }

    interface Interaction

    private class CaseDC : DiffUtil.ItemCallback<Case>() {
        override fun areItemsTheSame(
            oldItem: Case,
            newItem: Case
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Case,
            newItem: Case
        ) = oldItem == newItem
    }
}
