package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersBinding
import com.acruxcs.lawyer.model.User
import com.crazylegend.viewbinding.viewBinding
import java.util.function.Predicate

class LawyersFragment : Fragment(R.layout.fragment_lawyers),
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private lateinit var lawyersAdapter: LawyersListAdapter
    private val list = mutableListOf<User>()

    private val binding by viewBinding(FragmentLawyersBinding::bind)

    private lateinit var activityProgressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressLayout
        activityProgressLayout.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lawyersAdapter = LawyersListAdapter(parentFragmentManager, viewModel)

        with(binding) {
            lawyersRecycler.adapter = lawyersAdapter

            viewModel.getLawyers().observe(viewLifecycleOwner, {
                list.clear()
                list.addAll(it)
                lawyersAdapter.swapData(list)
                activityProgressLayout.visibility = View.GONE
            })

            lawyersFab.setOnClickListener {
                lawyersAdapter.swapData(list)
                FilterDialog(this@LawyersFragment).show(parentFragmentManager, "Filter")
            }
        }
    }

    override fun onFilterButtonClick(filter: MutableList<Predicate<User>>) {
        lawyersAdapter.swapData(viewModel.filter(list, filter))
    }
}

