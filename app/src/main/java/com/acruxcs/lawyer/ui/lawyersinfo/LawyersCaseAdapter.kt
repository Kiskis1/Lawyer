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
import kotlinx.android.synthetic.main.item_case.view.*
import java.text.DateFormat

class LawyersCaseAdapter :
    ListAdapter<Case, LawyersCaseAdapter.LawyersCaseViewHolder>(CaseDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyersCaseViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_case, parent, false)
    )

    override fun onBindViewHolder(holder: LawyersCaseViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Case>) {
        submitList(data.toMutableList())
    }

    inner class LawyersCaseViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

        }

        fun bind(item: Case) = with(itemView) {
            item_case_text_area.text = resources.getString(R.string.item_case_area, item.area)
            item_case_text_court.text = resources.getString(R.string.item_case_court, item.court)
            item_case_text_date.text = resources.getString(
                R.string.item_case_date,
                DateFormat.getDateInstance().format(item.date)
            )
            item_case_text_desc.text =
                resources.getString(R.string.item_case_short_description, item.shortDesc)
            item_case_text_outcome.text =
                resources.getString(R.string.item_case_outcome, item.outcome)
            item_case_text_type.text = resources.getString(R.string.item_case_type, item.type)
        }
    }

    private class CaseDC : DiffUtil.ItemCallback<Case>() {
        override fun areItemsTheSame(
            oldItem: Case,
            newItem: Case
        ) = oldItem.shortDesc == newItem.shortDesc

        override fun areContentsTheSame(
            oldItem: Case,
            newItem: Case
        ) = oldItem == newItem
    }
}
