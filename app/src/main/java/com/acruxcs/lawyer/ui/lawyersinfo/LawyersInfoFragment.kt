package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.metadata
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersInfoBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.profile.ProfileViewModel
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.toggleVisibilityInvisibleToVisible
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.snackbar.Snackbar

class LawyersInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private lateinit var lawyer: User
    private val lawyersCasesAdapter by lazy { LawyersCaseAdapter(this, null) }
    private val list = mutableListOf<Case>()
    private val viewModel: LawyersViewModel by viewModels()
    private val binding by viewBinding(FragmentLawyersInfoBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)!!
        }
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBackStack()
        viewModel.getStatus().observe(this) { handleStatus(it) }
        with(binding) {
            ViewCompat.setTransitionName(binding.imageProfile, lawyer.uid)
            speeddial.inflate(R.menu.menu_speed_dial)
            speeddial.setOnActionSelectedListener { item ->
                when (item.id) {
                    R.id.fab_call_lawyer -> {
                        Utils.showCallDialog(requireContext(), lawyer)
                        speeddial.close()
                    }
                    R.id.fab_message_lawyer -> {
                        findNavController().navigate(R.id.action_lawyersInfoFragment_to_questionFragment,
                            bundleOf(ARG_LAWYER to lawyer))
                        speeddial.close()
                    }
                    R.id.fab_book_lawyer -> {
                        findNavController().navigate(R.id.action_lawyersInfoFragment_to_newReservationFragment,
                            bundleOf(ARG_LAWYER to lawyer))
                        speeddial.close()
                    }
                }
                speeddial.close()
                false
            }

            recyclerView.adapter = lawyersCasesAdapter
            viewModel.getLawyersCases(lawyer.uid).observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    if (textEmptyCases.isVisible) textEmptyCases.toggleVisibilityInvisibleToVisible()
                    list.clear()
                    list.addAll(it)
                    lawyersCasesAdapter.swapData(list)
                } else
                    textEmptyCases.toggleVisibilityInvisibleToVisible()

            })

            textName.text =
                resources.getString(R.string.item_lawyer_name, lawyer.fullname)
            textEducation.text =
                resources.getString(R.string.item_lawyer_education, lawyer.education)
            textSpecialization.text =
                resources.getString(R.string.item_lawyer_specialization, lawyer.specialization)
            textCity.text = resources.getString(R.string.item_lawyer_city, lawyer.city)
            textExperience.text =
                resources.getQuantityString(
                    R.plurals.item_lawyer_experience,
                    lawyer.experience,
                    lawyer.experience
                )
            textWonCases.text =
                resources.getString(R.string.item_lawyer_number_of_won_cases, lawyer.wonCases)
            viewModel.getImageRef(lawyer.uid, object : ProfileViewModel.Companion.ImageCallback {
                override fun onCallback(value: String) {
                    imageProfile.load(value) {
                        error(R.drawable.ic_person_24)
                        if (imageProfile.metadata != null)
                            placeholderMemoryCacheKey(imageProfile.metadata!!.memoryCacheKey)
                    }
                }
            })
        }
    }

    private fun observeBackStack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Reservation>("reservation")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.createReservation(it)
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Question>("question")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.postQuestion(it)
            }
    }

    private fun handleStatus(it: Status?) {
        when (it) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()

            Status.ERROR -> shortToast(R.string.error_something)

            else -> shortToast(R.string.error_something)
        }
    }
}
