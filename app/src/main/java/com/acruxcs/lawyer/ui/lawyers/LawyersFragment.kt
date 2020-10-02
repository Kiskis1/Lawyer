package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.acruxcs.lawyer.R

class LawyersFragment : Fragment() {

    companion object {
        fun newInstance() = LawyersFragment()
    }

    private lateinit var viewModel: LawyersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lawyers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LawyersViewModel::class.java)
        // TODO: Use the ViewModel
    }
}