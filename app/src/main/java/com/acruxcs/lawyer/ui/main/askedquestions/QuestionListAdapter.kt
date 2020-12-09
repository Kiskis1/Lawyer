package com.acruxcs.lawyer.ui.main.askedquestions

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.ItemQuestionBinding
import com.acruxcs.lawyer.model.Question

class QuestionListAdapter :
    ListAdapter<Question, QuestionListAdapter.QuestionListViewHolder>(QuestionDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionListViewHolder(
        ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
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
        }

        fun bind(item: Question) = with(itemView) {
            with(ItemQuestionBinding.bind(itemView)) {
                textDescription.text =
                    resources.getString(R.string.item_question_description, item.description)
                textCountry.text =
                    resources.getString(R.string.item_question_country, item.country)
                textCity.text = resources.getString(R.string.item_question_city, item.city)
                textPhone.text = resources.getString(R.string.item_question_phone, item.phone)
                textFullname.text =
                    resources.getString(R.string.item_question_full_name, item.fullname)
            }
        }
    }

    private class QuestionDC : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(
            oldItem: Question,
            newItem: Question
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Question,
            newItem: Question
        ) = oldItem == newItem
    }
}
