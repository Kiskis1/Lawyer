package com.acruxcs.lawyer.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.databinding.DialogNewCaseBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.yes
import com.google.android.material.datepicker.MaterialDatePicker

class NewCaseDialog(private val fragment: Fragment, private val viewModel: ProfileViewModel) :
    DialogFragment() {
    private val case = Case()
    private val picker = MaterialDatePicker.Builder.datePicker().build()
    private lateinit var binding: DialogNewCaseBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewCaseBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)

            with(binding) {
                editDate.inputType = InputType.TYPE_NULL
                buttonAdd.setOnClickListener {
                    if (isValid()) {
                        case.shortDesc = editDescription.text.toString().trim()
                        case.court = editCourt.text.toString().trim()
                        case.area = editArea.text.toString().trim()
                        case.type = editType.text.toString().trim()
                        case.outcome = editOutcome.text.toString().trim()
                        viewModel.postCase(case)
                        Utils.hideKeyboard(requireContext(), binding.root)
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
                editDate.setOnClickListener {
                    picker.show(fragment.parentFragmentManager, "date_picker")
                    picker.addOnPositiveButtonClickListener {
                        case.date = it
                        editDate.setText(picker.headerText)
                    }
                }
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(editDescription, layoutDescription, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editCourt, layoutCourt, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editArea, layoutArea, requireContext()).yes { valid = false }
            checkFieldIfEmpty(editType, layoutType, requireContext()).yes { valid = false }
            checkFieldIfEmpty(editOutcome, layoutOutcome, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editDate, layoutDate, requireContext()).yes { valid = false }
        }
        return valid
    }
}
