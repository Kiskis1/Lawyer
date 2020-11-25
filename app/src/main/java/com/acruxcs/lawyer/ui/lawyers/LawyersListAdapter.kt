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
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.google.firebase.storage.StorageReference
import io.github.rosariopfernandes.firecoil.load
import kotlinx.android.synthetic.main.item_lawyer.view.*

class LawyersListAdapter(
    private val manager: FragmentManager,
    private val viewModel: LawyersViewModel
) :
    ListAdapter<User, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lawyer, parent, false)
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
            lawyers_text_name.text = resources.getString(R.string.lawyer_name, item.fullname)
            lawyers_text_education.text =
                resources.getString(R.string.lawyer_education, item.education)
            lawyers_text_specialization.text =
                resources.getString(R.string.lawyer_specialization, item.specialization)
            lawyers_text_experience.text =
                resources.getQuantityString(
                    R.plurals.lawyer_experience,
                    item.experience,
                    item.experience
                )
            lawyers_text_won_cases.text =
                resources.getString(R.string.lawyer_number_of_won_cases, item.wonCases)
            lawyers_text_city.text = resources.getString(R.string.lawyer_city, item.city)
            lawyers_button_call.setOnClickListener {
                Utils.showCallDialog(itemView.context, item)
            }
            viewModel.getImageRef(item.uid, object : MainViewModel.Companion.ImageCallback {
                override fun onCallback(value: StorageReference) {
                    lawyers_image_profile.load(value) {
                        crossfade(true)
                        error(R.drawable.ic_person_24)
                    }
                }
            })
            lawyers_button_question.setOnClickListener {
                Utils.showQuestionDialog(manager, item)
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

