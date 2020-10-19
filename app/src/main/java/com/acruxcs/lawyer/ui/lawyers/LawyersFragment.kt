package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.utils.Utils
import kotlinx.android.synthetic.main.fragment_lawyers.*

class LawyersFragment : Fragment(R.layout.fragment_lawyers), LawyersListAdapter.Interaction,
    FilterDialog.OnFilterButtonClickListener {
    private val viewModel: LawyersViewModel by viewModels()
    private lateinit var lawyersAdapter: LawyersListAdapter
    private val list = mutableListOf<Lawyer>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lawyersAdapter = LawyersListAdapter(this, parentFragmentManager)
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

    override fun onFilterButtonClick(filter: Map<String, String>) {
        lawyersAdapter.filter.filter(Utils.convertMap2String(filter))
    }
}
