package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import kotlinx.android.synthetic.main.fragment_lawyers.*
import java.util.function.Predicate

class LawyersFragment : Fragment(R.layout.fragment_lawyers),
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private lateinit var lawyersAdapter: LawyersListAdapter
    private val list = mutableListOf<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO: add progress bar later

        lawyersAdapter = LawyersListAdapter(parentFragmentManager, viewModel)
        lawyers_recycler.adapter = lawyersAdapter
        viewModel.getLawyers().observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            lawyersAdapter.swapData(list)
        })

        lawyers_fab.setOnClickListener {
            lawyersAdapter.swapData(list)
            FilterDialog(this).show(parentFragmentManager, "Filter")
        }
    }

    override fun onFilterButtonClick(filter: MutableList<Predicate<User>>) {
        lawyersAdapter.swapData(viewModel.filter(list, filter))
    }
}

