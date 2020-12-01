package com.acruxcs.lawyer.ui.main.reservation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentReservationsBinding
import com.crazylegend.viewbinding.viewBinding

class ReservationsFragment : Fragment(R.layout.fragment_reservations) {
    private val binding by viewBinding(FragmentReservationsBinding::bind)

    companion object {
        @JvmStatic
        fun newInstance() =
            ReservationsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
