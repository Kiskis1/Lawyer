package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.acruxcs.lawyer.ActivityViewModel
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentHistoryBinding
import com.acruxcs.lawyer.ui.main.reservation.ReservationsAdapter
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val reservationsAdapter by lazy { ReservationsAdapter() }
    private val binding by viewBinding(FragmentHistoryBinding::bind)
    private val viewModel: ActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.adapter = reservationsAdapter
            recyclerView.visibility = View.INVISIBLE
            viewModel.getPreviousReservationsForLawyer(MainApplication.user.value!!.uid)
                .observe(viewLifecycleOwner) {
                    reservationsAdapter.swapData(it)
                    if (it.isNotEmpty()) {
                        if (textNoItems.isVisible) textNoItems.toggleVisibilityGoneToVisible()
                    } else
                        if (textNoItems.isGone) textNoItems.toggleVisibilityGoneToVisible()
                    progressBar.progressLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
        }
    }
}
