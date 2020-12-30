package lt.viko.eif.lawyer.ui.lawyers

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
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
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.ItemLawyerBinding
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.utils.Utils

class LawyersListAdapter(
    private val fragment: Fragment,
) :
    ListAdapter<User, LawyersListAdapter.LawyerListViewHolder>(LawyerDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyerListViewHolder(
        ItemLawyerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LawyerListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<User>) {
        submitList(data.toMutableList())
    }

    inner class LawyerListViewHolder(
        val binding: ItemLawyerBinding,
    ) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
            val dir = LawyersFragmentDirections.actionLawyersFragmentToLawyersInfoFragment(clicked)
            val extra = FragmentNavigatorExtras(
                Pair(binding.infoLayout, ViewCompat.getTransitionName(binding.infoLayout)!!),
                Pair(binding.imageProfile, ViewCompat.getTransitionName(binding.imageProfile)!!),
                Pair(binding.textName, ViewCompat.getTransitionName(binding.textName)!!),
                Pair(binding.textEducation, ViewCompat.getTransitionName(binding.textEducation)!!),
                Pair(binding.textSpecialization,
                    ViewCompat.getTransitionName(binding.textSpecialization)!!),
                Pair(binding.textExperience,
                    ViewCompat.getTransitionName(binding.textExperience)!!),
                Pair(binding.textWonCases, ViewCompat.getTransitionName(binding.textWonCases)!!),
                Pair(binding.textLocation, ViewCompat.getTransitionName(binding.textLocation)!!),
                Pair(binding.textAddress, ViewCompat.getTransitionName(binding.textAddress)!!)
            )
            v!!.findNavController()
                .navigate(dir, extra)
        }

        fun bind(item: User) = with(itemView) {
            with(binding) {
                ViewCompat.setTransitionName(infoLayout, item.uid + "infoLayout")
                ViewCompat.setTransitionName(imageProfile, item.uid)
                ViewCompat.setTransitionName(textName, item.fullname)
                ViewCompat.setTransitionName(textEducation, item.uid + item.education)
                ViewCompat.setTransitionName(textSpecialization,
                    item.uid + item.specialization)
                ViewCompat.setTransitionName(textExperience,
                    item.uid + item.experience.toString())
                ViewCompat.setTransitionName(textWonCases,
                    item.uid + item.wonCases.toString())
                ViewCompat.setTransitionName(textLocation, item.uid + item.city)
                ViewCompat.setTransitionName(textAddress, item.uid + item.address)
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
                textLocation.text =
                    resources.getString(R.string.item_lawyer_location, item.country, item.city)
                buttonCall.setOnClickListener {
                    Utils.showCallDialog(itemView.context, item.phone, UserTypes.Lawyer)
                }
                imageProfile.load(item.imageRef) {
                    error(R.drawable.ic_person_24)
                    placeholderMemoryCacheKey(imageProfile.metadata?.memoryCacheKey)
                }
                buttonQuestion.setOnClickListener {
                    val dir =
                        LawyersFragmentDirections.actionLawyersFragmentToQuestionFragment(item,
                            null,
                            null)
                    fragment.findNavController().navigate(dir)
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

