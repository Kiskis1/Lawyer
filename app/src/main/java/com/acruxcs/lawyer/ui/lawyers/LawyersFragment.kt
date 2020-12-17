package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLawyersBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Status
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.snackbar.Snackbar
import java.util.function.Predicate

class LawyersFragment : Fragment(R.layout.fragment_lawyers),
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private val lawyersAdapter by lazy { LawyersListAdapter(this, viewModel) }
    private val list = mutableListOf<User>()

    val binding by viewBinding(FragmentLawyersBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Question>("question")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.postQuestion(it)
            }
        viewModel.getStatus().observe(this, { handleStatus(it) })
        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            recyclerView.adapter = lawyersAdapter

            viewModel.getLawyers().observe(viewLifecycleOwner, {
                list.clear()
                list.addAll(it)
                lawyersAdapter.swapData(list)
                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }

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

    private fun handleStatus(it: Status?) {
        when (it) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()

            Status.UPDATE_SUCCESS -> {
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()
            }
            Status.ERROR -> shortToast(R.string.error_something)

            else -> shortToast(R.string.error_something)
        }
    }
}

