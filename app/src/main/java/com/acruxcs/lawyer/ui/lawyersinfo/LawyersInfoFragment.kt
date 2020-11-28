package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.metadata
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersInfoBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.crazylegend.viewbinding.viewBinding

class LawyersInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private lateinit var lawyer: User
    private lateinit var lawyersCasesAdapter: LawyersCaseAdapter
    private val list = mutableListOf<Case>()
    private val viewModel: LawyersViewModel by viewModels()
    private val binding by viewBinding(FragmentLawyersInfoBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            lawyersinfoSpeeddial.inflate(R.menu.menu_speed_dial)
            lawyersinfoSpeeddial.setOnActionSelectedListener { item ->
                when (item.id) {
                    R.id.fab_call_lawyer -> {
                        Utils.showCallDialog(requireContext(), lawyer)
                        lawyersinfoSpeeddial.close()
                    }
                    R.id.fab_message_lawyer -> {
                        Utils.showQuestionDialog(parentFragmentManager, lawyer)
                        lawyersinfoSpeeddial.close()
                    }
                }
                false
            }

            lawyersCasesAdapter = LawyersCaseAdapter()
            lawyersinfoRecycler.adapter = lawyersCasesAdapter
            viewModel.getLawyersCases(lawyer.uid).observe(viewLifecycleOwner, {
                list.clear()
                list.addAll(it)
                lawyersCasesAdapter.swapData(list)
            })

            lawyersinfoTextName.text = resources.getString(R.string.lawyer_name, lawyer.fullname)
            lawyersinfoTextEducation.text =
                resources.getString(R.string.lawyer_education, lawyer.education)
            lawyersinfoTextSpecialization.text =
                resources.getString(R.string.lawyer_specialization, lawyer.specialization)
            lawyersinfoTextCity.text = resources.getString(R.string.lawyer_city, lawyer.city)
            lawyersinfoTextExperience.text =
                resources.getQuantityString(
                    R.plurals.lawyer_experience,
                    lawyer.experience,
                    lawyer.experience
                )
            lawyersinfoTextWonCases.text =
                resources.getString(R.string.lawyer_number_of_won_cases, lawyer.wonCases)
            viewModel.getImageRef(lawyer.uid, object : MainViewModel.Companion.ImageCallback {
                override fun onCallback(value: String) {
                    lawyersinfoImageProfile.load(value) {
                        crossfade(true)
                        error(R.drawable.ic_person_24)
                        placeholderMemoryCacheKey(lawyersinfoImageProfile.metadata?.memoryCacheKey)
                    }
                }
            })
        }
    }
}
