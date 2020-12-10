package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.metadata
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersInfoBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.profile.ProfileViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.acruxcs.lawyer.utils.Utils.toggleVisibility
import com.crazylegend.viewbinding.viewBinding

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            speeddial.inflate(R.menu.menu_speed_dial)
            speeddial.setOnActionSelectedListener { item ->
                when (item.id) {
                    R.id.fab_call_lawyer -> {
                        Utils.showCallDialog(requireContext(), lawyer)
                        speeddial.close()
                    }
                    R.id.fab_message_lawyer -> {
                        Utils.showQuestionDialog(parentFragmentManager, lawyer)
                        speeddial.close()
                    }
                    R.id.fab_book_lawyer -> {
                        ReservationDialog.newInstance(lawyer)
                            .show(parentFragmentManager, "reservation")
                        println("ASDDASSADASDASDASDASD")
                        speeddial.close()
                    }
                }
                speeddial.close()
                false
            }

            recyclerView.adapter = lawyersCasesAdapter
            viewModel.getLawyersCases(lawyer.uid).observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    if (textEmptyCases.isVisible) textEmptyCases.toggleVisibility()
                    list.clear()
                    list.addAll(it)
                    lawyersCasesAdapter.swapData(list)
                } else
                    textEmptyCases.toggleVisibility()

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
}
