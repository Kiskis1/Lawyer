package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import kotlinx.android.synthetic.main.fragment_lawyers.*
import java.util.function.Predicate
import java.util.stream.Collectors

class LawyersFragment : Fragment(R.layout.fragment_lawyers),
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private lateinit var lawyersAdapter: LawyersListAdapter
    private val list = mutableListOf<Lawyer>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO: add progress bar later

        lawyersAdapter = LawyersListAdapter(parentFragmentManager)
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

    companion object {
        val TAG = this::class.java.simpleName
        fun newInstance() = LawyersFragment()
    }

    override fun onFilterButtonClick(filter: MutableList<Predicate<Lawyer>>) {
        if (filter.size == 0) return
        val composite = filter.stream()
            .reduce({ w -> true }) { pred1, pred2 ->
                pred1.and(pred2)
            }
        val filtered = list.stream().filter(composite).collect(Collectors.toList())
        lawyersAdapter.swapData(filtered)
    }
}
