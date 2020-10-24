package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import kotlinx.android.synthetic.main.fragment_lawyers_info.*

class LawyersInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private lateinit var lawyer: Lawyer
    private lateinit var lawyersCasesAdapter: LawyersCaseAdapter
    private val list = mutableListOf<Case>()
    private val viewModel: LawyersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lawyersinfo_speeddial.inflate(R.menu.menu_speed_dial)
        lawyersinfo_speeddial.setOnActionSelectedListener { item ->
            when (item.id) {
                R.id.fab_call_lawyer -> {
                    Utils.showCallDialog(requireContext(), lawyer)
                    lawyersinfo_speeddial.close()
                }
                R.id.fab_message_lawyer -> {
                    Utils.showQuestionDialog(parentFragmentManager, lawyer)
                    lawyersinfo_speeddial.close()
                }
            }
            false
        }

        lawyersCasesAdapter = LawyersCaseAdapter()
        lawyersinfo_recycler.adapter = lawyersCasesAdapter
        viewModel.getLawyersCases().observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            lawyersCasesAdapter.swapData(list)
        })

        lawyersinfo_text_city.text = lawyer.city
        lawyersinfo_text_education.text = lawyer.education
        lawyersinfo_text_name.text = lawyer.name
        lawyersinfo_text_specialization.text = lawyer.specialization
    }
}
