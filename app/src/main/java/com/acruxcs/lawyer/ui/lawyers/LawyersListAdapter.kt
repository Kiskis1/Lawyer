package com.acruxcs.lawyer.ui.lawyers

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.metadata
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.ItemLawyerBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.profile.ProfileViewModel
import com.acruxcs.lawyer.utils.Utils

class LawyersListAdapter(
    private val manager: FragmentManager,
    private val viewModel: LawyersViewModel
) :
    ListAdapter<User, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        ItemLawyerBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    )

    override fun onBindViewHolder(holder: LawyerListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<User>) {
        submitList(data.toMutableList())
    }

    inner class LawyerListViewHolder(
        itemView: View,
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

        fun bind(item: User) = with(itemView) {
            with(ItemLawyerBinding.bind(itemView)) {
                lawyersTextName.text = resources.getString(R.string.item_lawyer_name, item.fullname)
                lawyersTextEducation.text =
                    resources.getString(R.string.item_lawyer_education, item.education)
                lawyersTextSpecialization.text =
                    resources.getString(R.string.item_lawyer_specialization, item.specialization)
                lawyersTextExperience.text =
                    resources.getQuantityString(
                        R.plurals.item_lawyer_experience,
                        item.experience,
                        item.experience
                    )
                lawyersTextWonCases.text =
                    resources.getString(R.string.item_lawyer_number_of_won_cases, item.wonCases)
                lawyersTextCountry.text =
                    resources.getString(R.string.item_lawyer_country, item.country)
                lawyersTextCity.text = resources.getString(R.string.item_lawyer_city, item.city)
                lawyersButtonCall.setOnClickListener {
                    Utils.showCallDialog(itemView.context, item)
                }
                viewModel.getImageRef(item.uid, object : ProfileViewModel.Companion.ImageCallback {
                    override fun onCallback(value: String) {
                        lawyersImageProfile.load(value) {
                            error(R.drawable.ic_person_24)
                            if (lawyersImageProfile.metadata != null)
                                placeholderMemoryCacheKey(lawyersImageProfile.metadata!!.memoryCacheKey)
                        }
                    }
                })
                lawyersButtonQuestion.setOnClickListener {
                    Utils.showQuestionDialog(manager, item)
                }
            }
        }
    }

    private class LawyerDC : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ) = oldItem.email == newItem.email

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ) = oldItem == newItem
    }
}

