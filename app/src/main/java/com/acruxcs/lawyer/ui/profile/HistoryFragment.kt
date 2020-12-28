package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentHistoryBinding
import com.acruxcs.lawyer.ui.main.reservation.ReservationsAdapter
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val reservationsAdapter by lazy { ReservationsAdapter(history = true) }
    private val binding by viewBinding(FragmentHistoryBinding::bind)
    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.toolbar.apply {
                title = resources.getString(R.string.title_history)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                menu.findItem(R.id.action_confirm).isVisible = false
            }

            progressBar.progressLayout.visibility = View.VISIBLE

            recyclerView.adapter = reservationsAdapter
            recyclerView.visibility = View.INVISIBLE
            viewModel.getPreviousReservationsForLawyer(MainActivity.user.value!!.uid)
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
