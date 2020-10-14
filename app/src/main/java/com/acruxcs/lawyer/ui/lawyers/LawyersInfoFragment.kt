package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import kotlinx.android.synthetic.main.fragment_lawyers_info.*

private const val ARG_LAWYER = "lawyer"

class LawyerInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private var lawyer: Lawyer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(lawyer)
        lawyersinfo_city.text = lawyer!!.city
        lawyersinfo_education.text = lawyer!!.education
        lawyersinfo_name.text = lawyer!!.nickname
        lawyersinfo_specialization.text = lawyer!!.specialization
    }

    companion object {
        @JvmStatic
        fun newInstance(lawyer: Lawyer) =
            LawyerInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_LAWYER, lawyer)

                }
            }
    }
}