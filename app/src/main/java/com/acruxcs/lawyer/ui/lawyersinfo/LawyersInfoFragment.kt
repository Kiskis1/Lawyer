package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.metadata
import com.acruxcs.lawyer.ActivityViewModel
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersInfoBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.snackbar.Snackbar

class LawyersInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private lateinit var lawyer: User
    private val lawyersCasesAdapter by lazy { LawyersCaseAdapter(this, null) }
    private val viewModel: LawyersViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLawyersInfoBinding::bind)
    private val args: LawyersInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lawyer = args.lawyer
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextViews()
        setTransitions()
        observeBackStack()
        viewModel.getStatus().observe(this) { handleStatus(it) }
        with(binding) {
            toolbar.toolbar.title = lawyer.fullname
            toolbar.toolbar.menu.findItem(R.id.action_confirm).isVisible = false
            speeddial.inflate(R.menu.menu_speed_dial)
            speeddial.setOnActionSelectedListener { item ->
                when (item.id) {
                    R.id.fab_call_lawyer -> {
                        Utils.showCallDialog(requireContext(), lawyer.phone, UserTypes.Lawyer)
                        speeddial.close()
                    }
                    R.id.fab_message_lawyer -> {
                        val dir =
                            LawyersInfoFragmentDirections.actionLawyersInfoFragmentToQuestionFragment(
                                lawyer, null, null)
                        findNavController().navigate(dir)
                        speeddial.close()
                    }
                    R.id.fab_book_lawyer -> {
                        val dir =
                            LawyersInfoFragmentDirections.actionLawyersInfoFragmentToNewReservationFragment(
                                lawyer, null, null)
                        findNavController().navigate(dir)
                        speeddial.close()
                    }
                }
                speeddial.close()
                false
            }

            recyclerView.adapter = lawyersCasesAdapter
            activityViewModel.getLawyersCases(lawyer.uid).observe(viewLifecycleOwner, {
                lawyersCasesAdapter.swapData(it)
                if (it.isNotEmpty()) {
                    if (textEmptyCases.isVisible) textEmptyCases.toggleVisibilityGoneToVisible()
                } else
                    if (textEmptyCases.isGone) textEmptyCases.toggleVisibilityGoneToVisible()
            })
        }
    }

    private fun setTextViews() {
        with(binding) {
            textName.text =
                resources.getString(R.string.item_lawyer_name, lawyer.fullname)
            textEducation.text =
                resources.getString(R.string.item_lawyer_education, lawyer.education)
            textSpecialization.text =
                resources.getString(R.string.item_lawyer_specialization, lawyer.specialization)
            textLocation.text =
                resources.getString(R.string.item_lawyer_location, lawyer.country, lawyer.city)
            textExperience.text =
                resources.getQuantityString(
                    R.plurals.item_lawyer_experience,
                    lawyer.experience,
                    lawyer.experience
                )
            textWonCases.text =
                resources.getString(R.string.item_lawyer_number_of_won_cases, lawyer.wonCases)
            textAddress.text = resources.getString(R.string.item_lawyer_address, lawyer.address)
            imageProfile.load(lawyer.imageRef) {
                error(R.drawable.ic_person_24)
                placeholderMemoryCacheKey(imageProfile.metadata?.memoryCacheKey)
            }
        }
    }

    private fun setTransitions() {
        ViewCompat.setTransitionName(binding.infoLayout, lawyer.uid + "infoLayout")
        ViewCompat.setTransitionName(binding.imageProfile, lawyer.uid)
        ViewCompat.setTransitionName(binding.textName, lawyer.fullname)
        ViewCompat.setTransitionName(binding.textEducation, lawyer.uid + lawyer.education)
        ViewCompat.setTransitionName(binding.textSpecialization, lawyer.uid + lawyer.specialization)
        ViewCompat.setTransitionName(binding.textExperience,
            lawyer.uid + lawyer.experience.toString())
        ViewCompat.setTransitionName(binding.textWonCases, lawyer.uid + lawyer.wonCases.toString())
        ViewCompat.setTransitionName(binding.textLocation, lawyer.uid + lawyer.city)
        ViewCompat.setTransitionName(binding.textAddress, lawyer.uid + lawyer.address)
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
