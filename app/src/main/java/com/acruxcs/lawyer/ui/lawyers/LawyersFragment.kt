package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
    private lateinit var progressLayout: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLayout = requireActivity().findViewById(R.id.activity_main_loading)
        progressLayout.visibility = View.VISIBLE

        lawyersAdapter = LawyersListAdapter(parentFragmentManager, viewModel)
        lawyers_recycler.adapter = lawyersAdapter
        viewModel.getLawyers().observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            lawyersAdapter.swapData(list)
            progressLayout.visibility = View.GONE
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

