package com.acruxcs.lawyer.ui.lawyers

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R

class LawyersFragment : Fragment(R.layout.fragment_lawyers) {

    companion object {
        fun newInstance() = LawyersFragment()
    }

    private val viewModel: LawyersViewModel by viewModels()

}