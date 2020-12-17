package com.acruxcs.lawyer.ui.lawyers

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
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
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER

class LawyersListAdapter(
    private val fragment: Fragment,
    private val viewModel: LawyersViewModel,
) :
    ListAdapter<User, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        ItemLawyerBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    )

    override fun onBindViewHolder(holder: LawyerListViewHolder, position: Int) {
        holder.bind(getItem(position))
        ViewCompat.setTransitionName(ItemLawyerBinding.bind(holder.itemView).imageProfile,
            getItem(position).uid)
    }

    fun swapData(data: List<User>) {
        submitList(data.toMutableList())
    }

    inner class LawyerListViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {
        private val binding = ItemLawyerBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
            val dir = LawyersFragmentDirections.actionLawyersFragmentToLawyersInfoFragment(clicked)
            val extra = FragmentNavigatorExtras(Pair(binding.imageProfile,
                ViewCompat.getTransitionName(binding.imageProfile)!!))
            v!!.findNavController()
                .navigate(dir, extra)
        }

        fun bind(item: User) = with(itemView) {
            with(binding) {
                textName.text = resources.getString(R.string.item_lawyer_name, item.fullname)
                textEducation.text =
                    resources.getString(R.string.item_lawyer_education, item.education)
                textSpecialization.text =
                    resources.getString(R.string.item_lawyer_specialization, item.specialization)
                textExperience.text =
                    resources.getQuantityString(
                        R.plurals.item_lawyer_experience,
                        item.experience,
                        item.experience
                    )
                textWonCases.text =
                    resources.getString(R.string.item_lawyer_number_of_won_cases, item.wonCases)
                textAddress.text = resources.getString(R.string.item_lawyer_address, item.address)
                textCountry.text =
                    resources.getString(R.string.item_lawyer_country, item.country)
                textCity.text = resources.getString(R.string.item_lawyer_city, item.city)
                buttonCall.setOnClickListener {
                    Utils.showCallDialog(itemView.context, item)
                }
                viewModel.getImageRef(item.uid, object : ProfileViewModel.Companion.ImageCallback {
                    override fun onCallback(value: String) {
                        imageProfile.load(value) {
                            error(R.drawable.ic_person_24)
                            if (imageProfile.metadata != null)
                                placeholderMemoryCacheKey(imageProfile.metadata!!.memoryCacheKey)
                        }
                    }
                })
                buttonQuestion.setOnClickListener {
                    fragment.findNavController()
                        .navigate(R.id.action_lawyersFragment_to_questionFragment,
                            bundleOf(ARG_LAWYER to item))
                }
            }
        }
    }

    private class LawyerDC : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User,
        ) = oldItem.email == newItem.email

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User,
        ) = oldItem == newItem
    }
}

