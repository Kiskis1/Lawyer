package com.acruxcs.lawyer.ui.lawyers

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import kotlinx.android.synthetic.main.item_lawyer.view.*

class LawyersListAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Lawyer, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lawyer, parent, false), interaction
    )

    override fun onBindViewHolder(holder: LawyerListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Lawyer>) {
        submitList(data.toMutableList())
    }

    inner class LawyerListViewHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
            val bundle = bundleOf()
            bundle.putParcelable("lawyer", clicked)
            v!!.findNavController()
                .navigate(R.id.action_lawyersFragment_to_lawyersInfoFragment, bundle)
        }

        fun bind(item: Lawyer) = with(itemView) {
            lawyers_text_name.text = resources.getString(R.string.lawyer_name, item.nickname)
            lawyers_text_education.text =
                resources.getString(R.string.lawyer_education, item.education)
            lawyers_text_specialization.text =
                resources.getString(R.string.lawyer_specialization, item.specialization)
            lawyers_text_experience.text =
                resources.getString(R.string.lawyer_experience, item.experience)
            lawyers_text_won_cases.text =
                resources.getString(R.string.lawyer_number_of_won_cases, item.won_cases)
        }
    }

    interface Interaction {

    }

    private class LawyerDC : DiffUtil.ItemCallback<Lawyer>() {
        override fun areItemsTheSame(
            oldItem: Lawyer,
            newItem: Lawyer
        ) = oldItem.email == newItem.email

        override fun areContentsTheSame(
            oldItem: Lawyer,
            newItem: Lawyer
        ) = oldItem == newItem
    }
}