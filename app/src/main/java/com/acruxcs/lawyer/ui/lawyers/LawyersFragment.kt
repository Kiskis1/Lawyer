package com.acruxcs.lawyer.ui.lawyers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import kotlinx.android.synthetic.main.fragment_lawyers.*

class LawyersFragment : Fragment(R.layout.fragment_lawyers), LawyersListAdapter.Interaction {
    private val viewModel: LawyersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lawyersAdapter = LawyersListAdapter(this)
        // lawyers_recycler.setHasFixedSize(true)
        lawyers_recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        lawyers_recycler.adapter = lawyersAdapter
        viewModel.getLawyers().observe(viewLifecycleOwner, {
            println(it)
            lawyersAdapter.swapData(it)
            println("ADASDASDASDASDASDASD")
        })
    }

    companion object {
        val TAG = this::class.java.simpleName
        fun newInstance() = LawyersFragment()
    }
}
