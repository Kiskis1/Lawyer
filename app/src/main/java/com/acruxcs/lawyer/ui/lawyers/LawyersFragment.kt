package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersBinding
import com.acruxcs.lawyer.model.User
import com.crazylegend.viewbinding.viewBinding
import java.util.function.Predicate

class LawyersFragment : Fragment(R.layout.fragment_lawyers),
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private val lawyersAdapter by lazy { LawyersListAdapter(parentFragmentManager, viewModel) }
    private val list = mutableListOf<User>()

    private val binding by viewBinding(FragmentLawyersBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            recyclerView.adapter = lawyersAdapter

            viewModel.getLawyers().observe(viewLifecycleOwner, {
                list.clear()
                list.addAll(it)
                lawyersAdapter.swapData(list)
                progressBar.progressLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            })

            fab.setOnClickListener {
                lawyersAdapter.swapData(list)
                FilterDialog(this@LawyersFragment).show(parentFragmentManager, "Filter")
            }
        }
    }

    override fun onFilterButtonClick(filter: MutableList<Predicate<User>>) {
        if (filter.isNotEmpty())
            lawyersAdapter.swapData(viewModel.filter(list, filter))
    }
}

