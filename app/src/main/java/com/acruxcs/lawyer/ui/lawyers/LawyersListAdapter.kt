package com.acruxcs.lawyer.ui.lawyers

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.removeFirst
import kotlinx.android.synthetic.main.item_lawyer.view.*

class LawyersListAdapter(
    private val interaction: Interaction? = null,
    private val manager: FragmentManager
) :
    ListAdapter<Lawyer, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()), Filterable {

    private val dataCopy = mutableListOf<Lawyer>()
    private val original = mutableListOf<Lawyer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lawyer, parent, false), interaction
    )

    override fun onBindViewHolder(holder: LawyerListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Lawyer>) {
        dataCopy.addAll(data)
        original.addAll(data)
        submitList(original.toMutableList())
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
            lawyers_text_name.text = resources.getString(R.string.lawyer_name, item.name)
            lawyers_text_education.text =
                resources.getString(R.string.lawyer_education, item.education)
            lawyers_text_specialization.text =
                resources.getString(R.string.lawyer_specialization, item.specialization)
            lawyers_text_experience.text =
                resources.getString(R.string.lawyer_experience, item.experience)
            lawyers_text_won_cases.text =
                resources.getString(R.string.lawyer_number_of_won_cases, item.won_cases)
            lawyers_text_city.text = item.city
            lawyers_button_call.setOnClickListener {
                Utils.showCallDialog(itemView.context)
            }
            lawyers_button_question.setOnClickListener {
                Utils.showQuestionDialog(manager)
            }
        }
    }

    interface Interaction

    private class LawyerDC : DiffUtil.ItemCallback<Lawyer>() {
        override fun areItemsTheSame(
            oldItem: Lawyer,
            newItem: Lawyer
        ) = oldItem.specialization == newItem.specialization

        override fun areContentsTheSame(
            oldItem: Lawyer,
            newItem: Lawyer
        ) = oldItem == newItem
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence): FilterResults {
                val filteredResults = mutableListOf<Lawyer>()
                filteredResults.clear()
                val constraint = Utils.convertString2Map(p0 as String)

                if (!constraint["city"].equals("")) {
                    filteredResults.addAll(original.filter { it.city == constraint["city"] })
                }
                if (!constraint["spec"].equals("") && filteredResults.isEmpty()) {
                    filteredResults.addAll(original.filter { it.specialization == constraint["spec"] })
                } else {
                    filteredResults.addAll(filteredResults.filter { it.specialization == constraint["spec"] })
                }
                if (!constraint["exp"].equals("") && filteredResults.isEmpty()) {
                    filteredResults.addAll(original.filter { it.experience == constraint["exp"]?.toIntOrNull() })
                } else {
                    filteredResults.addAll(filteredResults.filter { it.experience == constraint["exp"]?.toIntOrNull() })
                }

                filteredResults.removeFirst(original.size)

                println(filteredResults)
                val results = FilterResults()
                results.values = filteredResults
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults) {
                println(p1.values as List<Lawyer>)
                original.clear()
                notifyDataSetChanged()
                swapData(p1.values as List<Lawyer>)
            }
        }
    }
}

