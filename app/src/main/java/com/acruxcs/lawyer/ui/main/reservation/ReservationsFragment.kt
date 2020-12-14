package com.acruxcs.lawyer.ui.main.reservation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentReservationsBinding
import com.crazylegend.viewbinding.viewBinding

class ReservationsFragment : Fragment(R.layout.fragment_reservations) {
    private val binding by viewBinding(FragmentReservationsBinding::bind)
    private val viewModel: ReservationsViewModel by viewModels()

    private val reservationsAdapter by lazy { ReservationsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            recyclerView.adapter = reservationsAdapter

            viewModel.getReservationsForLawyer(MainApplication.user.value!!.uid)
                .observe(viewLifecycleOwner, {
                    reservationsAdapter.swapData(it)
                })

        }
    }

    companion object {

        fun newInstance() =
            ReservationsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
