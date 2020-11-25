package com.acruxcs.lawyer.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Question
import kotlinx.android.synthetic.main.item_question.view.*

class QuestionListAdapter :
    ListAdapter<Question, QuestionListAdapter.QuestionListViewHolder>(QuestionDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
    )

    override fun onBindViewHolder(holder: QuestionListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Question>) {
        submitList(data.toMutableList())
    }

    inner class QuestionListViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
        }

        fun bind(item: Question) = with(itemView) {
            main_asked_description.text = item.description
            main_asked_country.text = item.country
            main_asked_city.text = item.city
            main_asked_phone.text = item.phone
            main_asked_fullname.text = item.fullname
            main_asked_sender.text = item.sender
        }
    }

    private class QuestionDC : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(
            oldItem: Question,
            newItem: Question
        ) = oldItem.description == newItem.description

        override fun areContentsTheSame(
            oldItem: Question,
            newItem: Question
        ) = oldItem == newItem
    }
}
