package com.acruxcs.lawyer.ui.lawyersinfo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogReservationBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils

class ReservationDialog : DialogFragment() {
    private lateinit var binding: DialogReservationBinding
    private lateinit var lawyer: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(Utils.ARG_LAWYER)!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogReservationBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity, R.style.DialogTheme)
            with(binding) {
                toolbar.toolbar.setNavigationOnClickListener {
                    dismiss()
                }
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {

        }
        return valid
    }

    companion object {
        fun newInstance(lawyer: User) =
            ReservationDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(Utils.ARG_LAWYER, lawyer)
                }
            }
    }
}
